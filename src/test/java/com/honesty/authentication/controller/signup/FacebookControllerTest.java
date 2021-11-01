package com.honesty.authentication.controller.signup;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.honesty.authentication.jwt.JwtConfig;
import com.honesty.authentication.model.facebook.FacebookService;
import com.honesty.authentication.model.google.CustomOAuth2UserService;
import com.honesty.authentication.model.user_entity.UserEntity;
import com.honesty.authentication.model.user_entity.UserEntityService;
import com.honesty.authentication.user.ApplicationUser;
import com.honesty.authentication.user.ApplicationUserService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FacebookController.class)
@RunWith(SpringRunner.class)
class FacebookControllerTest {

    @Autowired ObjectMapper mapper;
    @Autowired private MockMvc mvc;

    @MockBean ApplicationUserService applicationUserService;
    @MockBean FacebookService facebookService;
    @MockBean JwtConfig jwtConfig;
    @MockBean UserEntityService userEntityService;

    @MockBean PasswordEncoder passwordEncoder;
    @MockBean private CustomOAuth2UserService customOAuth2UserService;
    @MockBean private ClientRegistrationRepository clientRepo;


    @Test
    void generateFacebookAuthorizeUrl() throws Exception{
        given(facebookService.generateFacebookAuthorizeUrl()).willReturn("Url test");
        RequestBuilder request = MockMvcRequestBuilders
                .get("/auth-api/facebook/generateFacebookAuthorizeUrl");

        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().string("Url test"))
                .andReturn();
    }

    @Test
    void generateFacebookAccessToken() throws Exception{
        given(facebookService.generateFacebookAccessToken("code test")).willReturn(new UserEntity());
        given(applicationUserService.getUserDetailsFromUserEntity(any())).willReturn(new ApplicationUser());
        given(jwtConfig.getAccessToken(any())).willReturn("access token test");
        given(jwtConfig.getRefreshToken(any())).willReturn("refresh token test");
        given(jwtConfig.getTokenPrefix()).willReturn("Bearer ");
        RequestBuilder request = MockMvcRequestBuilders
                .get("/auth-api/facebook")
                .param("code", "code test");

        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(header().string("access-token", "Bearer access token test"))
                .andExpect(header().string("refresh-token", "refresh token test"))
                .andReturn();
    }
}