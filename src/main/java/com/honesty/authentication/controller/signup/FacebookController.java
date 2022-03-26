package com.honesty.authentication.controller.signup;

import com.google.api.client.googleapis.auth.oauth2.GooglePublicKeysManager;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.gson.GsonFactory;

import com.google.api.client.http.LowLevelHttpRequest;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.honesty.authentication.jwt.JwtConfig;
import com.honesty.authentication.model.facebook.FacebookService;
import com.honesty.authentication.model.user_entity.UserEntity;
import com.honesty.authentication.user.ApplicationUserService;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.io.IOException;
import java.util.Collections;


@RestController
@RequestMapping(value = "auth-api/facebook", produces = "text/plain")
@Tag(name = "social api")
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
    public ResponseEntity<String> generateFacebookAuthorizeUrl(){
        return ResponseEntity.ok(facebookService.generateFacebookAuthorizeUrl()) ;
    }

    @GetMapping()
    @Operation(description = "406: code used")
    public ResponseEntity<String> generateFacebookAccessToken(@RequestParam("code") String code){
        try {
        UserEntity userEntity = facebookService.generateFacebookAccessToken(code);
        UserDetails userDetails = applicationUserService.getUserDetailsFromUserEntity(userEntity);
            String accessToken = jwtConfig.getAccessToken(userDetails);

            String refreshToken = jwtConfig.getRefreshToken(userDetails);

            HttpHeaders headers = new HttpHeaders();
            headers.add("access-token",jwtConfig.getTokenPrefix() +  accessToken);
            headers.add("refresh-token",refreshToken);
            headers.add("Access-Control-Expose-Headers", "access-token, refresh-token");

            return ResponseEntity.ok().headers(headers).body("connected with facebook");
        }catch (HttpClientErrorException e){
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("code user");
        }
    }

    @PostMapping("google")
    public String loginWithGoogle(){
        GooglePublicKeysManager manager = GooglePublicKeysManager();
        HttpTransport transport = new NetHttpTransport();
        JsonFactory factory = new GsonFactory();
        final String CLIENT_ID = "500761971574-7u2t3rvsakfoqv391jv4437arg1rvnul.apps.googleusercontent.com"
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder()
                // Specify the CLIENT_ID of the app that accesses the backend:
                .setAudience(Collections.singletonList(CLIENT_ID))
                // Or, if multiple clients access the backend:
                //.setAudience(Arrays.asList(CLIENT_ID_1, CLIENT_ID_2, CLIENT_ID_3))
                .build();

// (Receive idTokenString by HTTPS POST)

        GoogleIdToken idToken = verifier.verify(idTokenString);
        if (idToken != null) {
            Payload payload = idToken.getPayload();

            // Print user identifier
            String userId = payload.getSubject();
            System.out.println("User ID: " + userId);

            // Get profile information from payload
            String email = payload.getEmail();
            boolean emailVerified = Boolean.valueOf(payload.getEmailVerified());
            String name = (String) payload.get("name");
            String pictureUrl = (String) payload.get("picture");
            String locale = (String) payload.get("locale");
            String familyName = (String) payload.get("family_name");
            String givenName = (String) payload.get("given_name");

            // Use or store profile information
            // ...

        } else {
            System.out.println("Invalid ID token.");
        }
        return "OK";
    }

}
