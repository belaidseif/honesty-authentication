package com.honesty.authentication.controller.signup.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class ChangePasswordReqDto {

    @NotNull
    @Size(min = 6, max = 60, message = "size should be between 6 and 60")
    private String ancientPassword;

    @NotNull
    @Size(min = 6, max = 60, message = "size should be between 6 and 60")
    private String newPassword;
}
