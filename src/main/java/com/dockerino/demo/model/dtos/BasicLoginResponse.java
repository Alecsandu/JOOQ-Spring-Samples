package com.dockerino.demo.model.dtos;

public record BasicLoginResponse(
        String accessToken,
        UserInfo userInfo
) {
}