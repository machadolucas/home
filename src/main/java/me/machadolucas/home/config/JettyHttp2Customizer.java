package me.machadolucas.home.config;

import org.eclipse.jetty.alpn.server.ALPNServerConnectionFactory;
import org.eclipse.jetty.http2.HTTP2Cipher;
import org.eclipse.jetty.http2.server.HTTP2ServerConnectionFactory;
import org.eclipse.jetty.server.ConnectionFactory;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.jetty.JettyEmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.jetty.JettyServerCustomizer;
import org.springframework.stereotype.Component;

/**
 * This {@link EmbeddedServletContainerCustomizer} will customize embedded Jetty configuration to:
 * <ul>
 * <li>update the SSLContextFactory to select the appropriate TLS cipher for HTTP/2 using {@code HTTP2Cipher.COMPARATOR}
 * <li>replace the ConnectionFactories configured by Boot by {@link ALPNServerConnectionFactory} and
 * {@link HTTP2ServerConnectionFactory}
 * </ul>
 * 
 * @author Brian Clozel
 */

@Component
public class JettyHttp2Customizer implements EmbeddedServletContainerCustomizer {

    private final ServerProperties serverProperties;

    @Autowired
    public JettyHttp2Customizer(final ServerProperties serverProperties) {
        this.serverProperties = serverProperties;
    }

    @Override
    public void customize(final ConfigurableEmbeddedServletContainer container) {
        final JettyEmbeddedServletContainerFactory factory = (JettyEmbeddedServletContainerFactory) container;

        factory.addServerCustomizers(new JettyServerCustomizer() {
            @Override
            public void customize(final Server server) {
                if (JettyHttp2Customizer.this.serverProperties.getSsl() != null
                        && JettyHttp2Customizer.this.serverProperties.getSsl().isEnabled()) {
                    final ServerConnector connector = (ServerConnector) server.getConnectors()[0];
                    final int port = connector.getPort();
                    final SslContextFactory sslContextFactory = connector
                            .getConnectionFactory(SslConnectionFactory.class).getSslContextFactory();
                    final HttpConfiguration httpConfiguration = connector
                            .getConnectionFactory(HttpConnectionFactory.class).getHttpConfiguration();

                    configureSslContextFactory(sslContextFactory);
                    final ConnectionFactory[] connectionFactories = createConnectionFactories(sslContextFactory,
                            httpConfiguration);

                    final ServerConnector serverConnector = new ServerConnector(server, connectionFactories);
                    serverConnector.setPort(port);
                    // override existing connectors with new ones
                    server.setConnectors(new Connector[] { serverConnector });
                }
            }

            private void configureSslContextFactory(final SslContextFactory sslContextFactory) {
                sslContextFactory.setCipherComparator(HTTP2Cipher.COMPARATOR);
                sslContextFactory.setUseCipherSuitesOrder(true);
            }

            private ConnectionFactory[] createConnectionFactories(final SslContextFactory sslContextFactory,
                    final HttpConfiguration httpConfiguration) {
                final SslConnectionFactory sslConnectionFactory = new SslConnectionFactory(sslContextFactory, "alpn");
                final ALPNServerConnectionFactory alpnServerConnectionFactory = new ALPNServerConnectionFactory("h2",
                        "h2-17", "h2-16", "h2-15", "h2-14");

                final HTTP2ServerConnectionFactory http2ServerConnectionFactory = new HTTP2ServerConnectionFactory(
                        httpConfiguration);

                return new ConnectionFactory[] { sslConnectionFactory, alpnServerConnectionFactory,
                        http2ServerConnectionFactory };
            }
        });
    }
}