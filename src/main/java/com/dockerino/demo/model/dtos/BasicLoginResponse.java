package com.dockerino.demo.model.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BasicLoginResponse {
    private String accessToken;
    private UserInfo userInfo;

    public BasicLoginResponse(String accessToken, UserInfo userInfo) {
        this.accessToken = accessToken;
        this.userInfo = userInfo;
    }
}