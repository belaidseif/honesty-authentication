package com.honesty.authentication.controller.signup;

import com.honesty.authentication.jwt.JwtConfig;
import com.honesty.authentication.model.facebook.FacebookService;
import com.honesty.authentication.model.user_entity.UserEntity;
import com.honesty.authentication.user.ApplicationUserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;


@RestController
@RequestMapping("auth-api/facebook")
@Tag(name = "facebook api")
public class FacebookController {


    private final ApplicationUserService applicationUserService;
    private final FacebookService facebookService;
    private final JwtConfig jwtConfig;


    public FacebookController(ApplicationUserService applicationUserService, FacebookService facebookService, JwtConfig jwtConfig) {
        this.applicationUserService = applicationUserService;
        this.facebookService = facebookService;
        this.jwtConfig = jwtConfig;
    }



    @GetMapping("generateFacebookAuthorizeUrl")
    public String generateFacebookAuthorizeUrl(){
        return facebookService.generateFacebookAuthorizeUrl();
    }

    @GetMapping()
    @Operation(description = "400: code used")
    public ResponseEntity<String> generateFacebookAccessToken(@RequestParam("code") String code){
        try {
        UserEntity userEntity = facebookService.generateFacebookAccessToken(code);
        UserDetails userDetails = applicationUserService.getUserDetailsFromUserEntity(userEntity);
            String accessToken = jwtConfig.getAccessToken(userDetails);

            String refreshToken = jwtConfig.getRefreshToken(userDetails);

            HttpHeaders headers = new HttpHeaders();
            headers.add("access-token",jwtConfig.getTokenPrefix() +  accessToken);
            headers.add("refresh-token",refreshToken);

            return ResponseEntity.ok().headers(headers).body("connected with facebook");
        }catch (HttpClientErrorException e){
            return ResponseEntity.badRequest().body("code used");
        }
    }

}
