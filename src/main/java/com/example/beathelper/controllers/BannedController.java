package com.example.beathelper.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BannedController {
    @GetMapping("/banned")
    public String bannedPage(){
        return "banned";
    }
}
