package com.honesty.authentication.controller.signup;

import com.honesty.authentication.controller.signup.dto.ChangePasswordReqDto;
import com.honesty.authentication.model.token.ConfirmationTokenService;
import com.honesty.authentication.model.user_entity.UserEntityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.UUID;

@AllArgsConstructor
@RestController
@RequestMapping("auth-api/management")
@Tag(name = "management api")
public class ManagementController {


    private final ConfirmationTokenService confirmationTokenService;
    private final UserEntityService userEntityService;

    @GetMapping("test")
    public String getTest(){
        return  "test authentication";
    }
    @GetMapping()
    @Operation(description = "404: token not found\n406:token expired\n409:not same user")
    public ResponseEntity<String> verifyAccountWithEmailToken(@RequestParam("token") String token, HttpServletRequest request){
        confirmationTokenService.confirmToken(token, getUserUid(request));
        return ResponseEntity.ok().body("your account is enabled now");
    }

    @GetMapping("send-new-token")
    public ResponseEntity<String> sendNewVerificationEmailToken(HttpServletRequest request){

        userEntityService.sendNewToken(getUserUid(request));
        return ResponseEntity.status(HttpStatus.ACCEPTED).body("token was sent");
    }

    @PostMapping("change-password")
    @Operation(description = "406:password does not match")
    public ResponseEntity<String> changePassword(@RequestBody @Valid ChangePasswordReqDto changePassword, HttpServletRequest request){
        userEntityService.changePassword(changePassword.getAncientPassword(), changePassword.getNewPassword(),getUserUid(request) );

        return ResponseEntity.status(HttpStatus.CREATED).body("password has been changed");
    }

    private UUID getUserUid(HttpServletRequest request){
        return UUID.fromString(String.valueOf(request.getAttribute("username")));
    }
}
