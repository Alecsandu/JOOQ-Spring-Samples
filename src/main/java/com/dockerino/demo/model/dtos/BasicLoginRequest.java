package com.dockerino.demo.model.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
public class BasicLoginRequest {
    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Length(min = 12, max = 128)
    private String password;
}