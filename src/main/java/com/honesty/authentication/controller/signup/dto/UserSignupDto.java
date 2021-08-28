package com.honesty.authentication.controller.signup.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSignupDto {

    @Email(message = "email format invalid")
    @NotNull
    @NotBlank
    private String email;

    @Size(min = 6, max = 60, message = "size should be between 6 and 60")
    @NotNull
    private String password;

    @PastOrPresent
    @NotNull
    private LocalDate birthdate;

}
