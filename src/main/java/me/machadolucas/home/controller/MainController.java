package me.machadolucas.home.controller;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.jetty.server.Dispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import me.machadolucas.home.util.BackgroundImageUtils;

@Controller
public class MainController {

    @Autowired
    BackgroundImageUtils backgroundImageUtils;

    @RequestMapping("/")
    public String index(final Model model, final HttpServletRequest request) {
        final Dispatcher dispatcher = (Dispatcher) request.getRequestDispatcher("/talks.json");
        dispatcher.push(request);

        model.addAttribute("randomBackground", this.backgroundImageUtils.getRandomPhoto());
        return "index";
    }

    @RequestMapping("/pages/{page}.html")
    public String pages(@PathVariable("page") final String page, final Model model) {

        if ("photography".equals(page)) {
            model.addAttribute("photosUrls", this.backgroundImageUtils.getPhotos());
        }

        return "pages/" + page;
    }
}
