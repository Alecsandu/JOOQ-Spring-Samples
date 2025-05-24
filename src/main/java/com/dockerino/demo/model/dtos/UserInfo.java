package com.dockerino.demo.model.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class UserInfo {
    private UUID id;
    private String email;
    private String name;
}