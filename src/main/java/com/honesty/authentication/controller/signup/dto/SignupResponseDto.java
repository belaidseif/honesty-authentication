package com.honesty.authentication.controller.signup.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class SignupResponseDto {

    @NotNull
    private String email;

    @NotNull
    private String password;
}
