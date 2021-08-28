package com.honesty.authentication.model.facebook;

import lombok.Data;

import java.time.LocalDate;

@Data
public class FacebookUser {
    private String id;
    private String first_name;
    private String name;
    private LocalDate birthdate;
    private String hometown;

}
