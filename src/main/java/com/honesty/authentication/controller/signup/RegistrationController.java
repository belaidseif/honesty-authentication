package com.honesty.authentication.controller.signup;


import com.honesty.authentication.controller.signup.dto.ForgetPasswordReqDto;
import com.honesty.authentication.controller.signup.dto.ResetPasswordReqDto;
import com.honesty.authentication.controller.signup.dto.SignupResDto;
import com.honesty.authentication.controller.signup.dto.SignupReqDto;


import com.honesty.authentication.model.token.ConfirmationTokenService;
import com.honesty.authentication.model.user_entity.UserEntityService;
import io.swagger.v3.oas.annotations.Operation;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import javax.validation.Valid;

@RestController
@RequestMapping("auth-api/registration")
@Tag(name = "registration api")
public class RegistrationController {
    @Autowired
    UserEntityService userService;
    @Autowired
    ConfirmationTokenService confirmationTokenService;


    @GetMapping
    public String test(){
        return "test signup";
    }



    @PostMapping()
    @Operation(description = "409: duplicate user")
    public ResponseEntity<SignupResDto> signupNewUser(@RequestBody @Valid SignupReqDto signupReqDto){
        SignupResDto response =  userService.addUser(signupReqDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("forget-password")
    @Operation(description = "404: email not found")
    public ResponseEntity<String> sendEmailForgetPassword(@RequestBody @Valid ForgetPasswordReqDto forgetPassword){
        userService.sendForgetPasswordEmail(forgetPassword.getEmail());

        return ResponseEntity.status(HttpStatus.ACCEPTED).body("an email was sent");

    }

    @PostMapping("reset-password")
    @Operation(description = "404: token not found\n406: token expired")
    public ResponseEntity<String> resetPasswordUsingToken(@RequestBody @Valid ResetPasswordReqDto resetPassword){
        confirmationTokenService.resetPasswordUsingToken(resetPassword.getToken(), resetPassword.getPassword());

        return ResponseEntity.status(HttpStatus.CREATED).body("The password has been reset with success");
    }

}
