package me.machadolucas.home.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
public class ErrorsController {

    @RequestMapping("/403")
    public String e403(final Model model) {
        return "error/403";
    }

    @RequestMapping("/404")
    public String e404(final Model model) {
        return "error/404";
    }

    @RequestMapping("/500")
    public String e500(final Model model) {
        return "error/500";
    }

}
