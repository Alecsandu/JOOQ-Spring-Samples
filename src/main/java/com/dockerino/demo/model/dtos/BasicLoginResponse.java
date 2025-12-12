package com.dockerino.demo.model.dtos;

import org.jspecify.annotations.NonNull;

public record BasicLoginResponse(
        String accessToken
) {
    @Override
    @NonNull
    public String toString() {
        return "BasicLoginResponse{" +
                "accessToken='" + accessToken + '\'' +
                '}';
    }
}
