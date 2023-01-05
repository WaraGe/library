package com.korit.library.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/account")
public class AccountController {

    @GetMapping("/login")
    public String loadIndex() {
        return "account/login"; // account폴더가 있으므로 경로지정
    }

    @GetMapping("/register")
    public String register() {
        return "account/register";
    }
}
