package com.korit.library.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/account")
public class AccountController {

    @GetMapping("/login")
    public String loadIndex() {
        return "account/login"; // account폴더가 있으므로 경로지정
    }

    @PostMapping("/login/error")
    public String loginError() {
        return "account/login_error"; // account폴더가 있으므로 경로지정
    }
    // 데이터 받는쪽은 get요청이지만 ForwardUrl을 받기 위해서는 post로 받기 위한것
    
    @GetMapping("/register")
    public String register() {
        return "account/register";
    }
}
