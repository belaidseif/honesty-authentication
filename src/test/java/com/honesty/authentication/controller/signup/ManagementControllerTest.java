package com.honesty.authentication.controller.signup;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.honesty.authentication.jwt.JwtConfig;
import com.honesty.authentication.model.google.CustomOAuth2UserService;
import com.honesty.authentication.model.token.ConfirmationTokenService;
import com.honesty.authentication.model.user_entity.UserEntityService;
import com.honesty.authentication.user.ApplicationUserService;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ManagementController.class)
@RunWith(SpringRunner.class)
class ManagementControllerTest {

    @Autowired ObjectMapper mapper;
    @Autowired private MockMvc mvc;
    private String bearerToken;

    @MockBean ConfirmationTokenService confirmationTokenService;
    @MockBean UserEntityService userEntityService;

    @MockBean
    PasswordEncoder passwordEncoder;
    @MockBean private ApplicationUserService applicationUserService;
    @MockBean private JwtConfig jwtConfig;
    @MockBean private CustomOAuth2UserService customOAuth2UserService;
    @MockBean private ClientRegistrationRepository clientRepo;

    @BeforeEach
    void setUp(){
        given(jwtConfig.getTokenPrefix()).willReturn("Bearer ");
        given(jwtConfig.getSecretKey()).willReturn(Keys.hmacShaKeyFor("chqbqfdf84dsfq54fsdf6q54sdf6q5s4dc15".getBytes()));
        bearerToken = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI1Yjc2NTE2OC0yNmU4LTQxODAtYmZhZC03N2I3NDdjMzU1MjQiLCJhdXRob3JpdGllcyI6W3siYXV0aG9yaXR5IjoiUk9MRV9NRU1CRVIifSx7ImF1dGhvcml0eSI6ImNvbW1lbnQ6d3JpdGUifSx7ImF1dGhvcml0eSI6InBvc3Q6d3JpdGUifSx7ImF1dGhvcml0eSI6Im1lc3NhZ2U6d3JpdGUifV0sImlhdCI6MTYzMDg0MDk0MiwiZXhwIjoxNzUxNzU2NDAwfQ.kTeMd7XygK7PXcK5XVwuUmg1pBD1dGilNKobjW5jObg";
    }
    @Test
    void it_should_call_confirmToken_when_verifyAccountWithEmailToken_when_is_called() throws Exception{

        RequestBuilder request = MockMvcRequestBuilders
                .get("/auth-api/management")
                .header("Authorization", bearerToken)
                .param("token", "token sample");

        mvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();
        verify(confirmationTokenService).confirmToken("token sample", UUID.fromString("5b765168-26e8-4180-bfad-77b747c35524"));
    }

    @Test
    void it_should_return_401_when_the_token_is_invalid() throws Exception{
        RequestBuilder request = MockMvcRequestBuilders
                .get("/auth-api/management")
                .header("Authorization", "Bearer eyJQ3JpdW5jObg")
                .param("token", "token sample");
        mvc.perform(request)
                .andExpect(status().isUnauthorized())
                .andReturn();
        verify(confirmationTokenService, never()).confirmToken(any(),any());
    }

    @Test
    void it_should_call_sendNewToken_when_sendNewVerificationEmailToken_is_called() throws Exception{
        RequestBuilder request = MockMvcRequestBuilders
                .get("/auth-api/management/send-new-token")
                .header("Authorization", bearerToken);

        mvc.perform(request)
                .andExpect(status().isAccepted())
                .andReturn();
        verify(userEntityService).sendNewToken(UUID.fromString("5b765168-26e8-4180-bfad-77b747c35524"));
    }

    @Test
    void it_should_call_changePassword_when_the_request_body_is_correct() throws Exception{
        Map<String, String> map = Map.of(
                "ancientPassword", "123456",
                "newPassword", "password"
        );

        RequestBuilder request = MockMvcRequestBuilders
                .post("/auth-api/management/change-password")
                .header("Authorization", bearerToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(map));

        mvc.perform(request)
                .andExpect(status().isCreated())
                .andReturn();

        verify(userEntityService).changePassword(
                "123456",
                "password",
                UUID.fromString("5b765168-26e8-4180-bfad-77b747c35524")
        );
    }

    @Test
    void it_not_should_call_changePassword_when_the_request_body_is_not_correct() throws Exception{
        Map<String, String> map = Map.of(
                "ancientPassword", "123456",
                "newPassword", "pass"
        );

        RequestBuilder request = MockMvcRequestBuilders
                .post("/auth-api/management/change-password")
                .header("Authorization", bearerToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(map));

        mvc.perform(request)
                .andExpect(status().isBadRequest())
                .andReturn();

        verify(userEntityService, never()).changePassword(any(),any(),any());
    }
}