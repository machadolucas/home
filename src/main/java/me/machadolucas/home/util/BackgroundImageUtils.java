package me.machadolucas.home.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import lombok.extern.java.Log;

import org.springframework.stereotype.Component;

@Log
@Component
public class BackgroundImageUtils {

    private List<String> photos = new ArrayList<String>();

    public String getRandomPhoto() {
        return photos.get(ThreadLocalRandom.current().nextInt(photos.size()));
    }

    public BackgroundImageUtils() {
        super();
        URL url;
        try {
            // Get the RSS as a plain text webpage
            url = new URL("http://backend.deviantart.com/rss.xml?q=gallery%3A0-sirluke-0%2F12756273&type=deviation");
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuilder webpage = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                webpage.append(inputLine);
            }
            in.close();

            // Parse the text by media:content tags and add the URLs to list
            int index = 0;
            while (index <= webpage.length()) {
                index = webpage.indexOf("media:content url=\"") + 19;
                if (index == -1) {
                    break;
                }
                String link = webpage.substring(index, index + 130);
                int quoteIndex = link.indexOf("\"");
                link = link.substring(0, quoteIndex);
                photos.add(link);
                webpage = new StringBuilder(webpage.substring(index + quoteIndex + 2));
            }
            log.info("Detected image links:" + photos.size());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
