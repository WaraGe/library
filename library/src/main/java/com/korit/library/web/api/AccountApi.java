package com.korit.library.web.api;

import com.korit.library.aop.annotation.ValidAspect;
import com.korit.library.service.AccountService;
import com.korit.library.web.dto.CMRespDto;
import com.korit.library.web.dto.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/api/account")
public class AccountApi {

    @Autowired
    private AccountService accountService;

    @ValidAspect
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid UserDto userDto, BindingResult bindingResult) {

        accountService.duplicateUsername(userDto.getUsername()); // 검사
        accountService.comparePassword(userDto.getPassword(), userDto.getRepassword()); // 기존패스워드와 리패스워드와 비교

        UserDto user = accountService.registerUser(userDto);

        return ResponseEntity
                .created(URI.create("/api/account/user/" + user.getUserId()))
                .body(new CMRespDto<>(HttpStatus.CREATED.value(),"Create a new User",user));
    }
//  user객체 가져오는 방법
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUser(@PathVariable int userId) {
        return ResponseEntity
                .ok()
                .body(new CMRespDto(HttpStatus.OK.value(),"Successfully", accountService.getUser(userId)));

    }
}
