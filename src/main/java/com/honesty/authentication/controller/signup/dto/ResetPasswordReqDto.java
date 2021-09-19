package com.honesty.authentication.controller.signup.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class ResetPasswordReqDto {

    @NotNull
    @Size(min = 6, max = 60, message = "size should be between 6 and 60")
    private String password;

    @NotNull
    @NotBlank
    private String token;
}
