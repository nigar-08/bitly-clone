package com.url_shortener.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SpaController {

    @GetMapping({
            "/",
            "/about",
            "/register",
            "/login",
            "/dashboard",
            "/error",
            "/s/{path:[^\\.]*}"
    })
    public String forwardToApp() {
        return "forward:/index.html";
    }
}
