package me.machadolucas.home.controller;

import me.machadolucas.home.util.BackgroundImageUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class MainController {

    @Autowired
    BackgroundImageUtils backgroundImageUtils;

    @RequestMapping("/")
    public String index(Model model) {

        model.addAttribute("randomBackground", backgroundImageUtils.getRandomPhoto());
        return "index";
    }

    @RequestMapping("/pages/{page}.html")
    public String pages(@PathVariable("page") String page, Model model) {

        return "pages/" + page;
    }
}
