package com.dockerino.demo.api;

import com.dockerino.demo.model.dtos.UserInfo;
import com.dockerino.demo.service.AccountsService;
import jakarta.validation.constraints.Email;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/accounts")
public class AccountsApi {

    private final AccountsService accountsService;

    public AccountsApi(AccountsService accountsService) {
        this.accountsService = accountsService;
    }

    @GetMapping("/userinfo")
    public ResponseEntity<UserInfo> getUserInfo(@RequestParam @Email String email) {
        UserInfo userInfo = accountsService.getUserInfo(email);
        return ResponseEntity.ok(userInfo);
    }

}
