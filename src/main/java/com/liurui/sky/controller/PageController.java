package com.liurui.sky.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/admin/")
    public String admin() {
        return "forward:/admin/index.html";
    }

    @GetMapping("/user/")
    public String user() {
        return "forward:/user/index.html";
    }
}
