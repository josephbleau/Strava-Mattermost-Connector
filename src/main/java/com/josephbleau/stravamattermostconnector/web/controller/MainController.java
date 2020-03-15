package com.josephbleau.stravamattermostconnector.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@SuppressWarnings("SameReturnValue")
@Controller
@RequestMapping("/")
public class MainController {

    @GetMapping
    public String index(
            Model model,
            final @RequestParam(value = "settings", required = false) String settings) {

        if(settings != null) {
            model.addAttribute("settings", settings);
        } else {
            model.addAttribute("settings", "");
        }

        return "index";
    }

}
