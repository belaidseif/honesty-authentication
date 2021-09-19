package com.honesty.authentication.controller.signup.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class ForgetPasswordReqDto {

    @NotNull
    @Email
    @NotBlank
    private String email;
}
