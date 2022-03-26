package com.honesty.authentication.controller.signup.dto;


import com.honesty.authentication.model.user_entity.Gender;
import com.honesty.authentication.model.user_entity.UserEntity;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.ZonedDateTime;

import java.util.UUID;

@Data
public class UserInfoResDto {

    @NotNull
    private UUID id;

    private String username;
    @NotNull
    private ZonedDateTime createdAt;


    private LocalDate birthdate;
    private Gender gender;

    @NotNull
    private boolean isVerified;

    public static UserInfoResDto getUserInfoFromUserEntity(UserEntity user){
        UserInfoResDto userInfo = new UserInfoResDto();
        userInfo.id = user.getId();
        userInfo.username = user.getUsername();
        userInfo.createdAt = user.getCreatedAt();
        userInfo.birthdate = user.getBirthdate();
        userInfo.gender = user.getGender();
        userInfo.isVerified = user.isVerified();

        return userInfo;
    }

}
