package com.josephbleau.stravamattermostconnector.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@SuppressWarnings("SameReturnValue")
@Controller
@RequestMapping("/")
public class MainController {

    @GetMapping
    public String index() {
        return "index";
    }

}
