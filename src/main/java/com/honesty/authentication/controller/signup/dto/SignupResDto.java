package com.honesty.authentication.controller.signup.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
public class SignupResDto {

    @NotNull
    private String email;

    @NotNull
    private String password;
}
