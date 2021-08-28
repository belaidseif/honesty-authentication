package com.honesty.authentication.controller.signup;


import com.honesty.authentication.controller.signup.dto.SignupResponseDto;
import com.honesty.authentication.controller.signup.dto.UserSignupDto;

import com.honesty.authentication.model.user_entity.UserEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import javax.validation.Valid;

@RestController
@RequestMapping("auth-api/signup")
public class SignupController {
    @Autowired
    UserEntityService userService;


    @GetMapping
    public String test(){
        return "test";
    }



    @PostMapping()
    public ResponseEntity<SignupResponseDto> addUser(@RequestBody @Valid UserSignupDto userSignupDto){
        SignupResponseDto response =  userService.addUser(userSignupDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);


    }
}
