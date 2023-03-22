package com.ecinema.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class Webpage {

    @GetMapping("/Cinema.html")
    public String getMainPage(Model model){
        return "Cinema";
    }
}
