package com.honesty.authentication.controller.signup;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.honesty.authentication.controller.signup.dto.SignupResDto;
import com.honesty.authentication.jwt.JwtConfig;
import com.honesty.authentication.model.google.CustomOAuth2UserService;
import com.honesty.authentication.model.token.ConfirmationTokenService;
import com.honesty.authentication.model.user_entity.UserEntityService;
import com.honesty.authentication.user.ApplicationUserService;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.crypto.SecretKey;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RegistrationController.class)
@RunWith(SpringRunner.class)
class RegistrationControllerTest {

    @Autowired ObjectMapper mapper;
    @Autowired private MockMvc mvc;

    @MockBean UserEntityService userService;
    @MockBean
    ConfirmationTokenService confirmationTokenService;

    @MockBean PasswordEncoder passwordEncoder;
    @MockBean private ApplicationUserService applicationUserService;
    @MockBean private JwtConfig jwtConfig;
    @MockBean private CustomOAuth2UserService customOAuth2UserService;
    @MockBean private ClientRegistrationRepository clientRepo;


    @Test
    void it_should_add_user_and_return_his_information() throws Exception{

        Map<String, String> map = Map.of(
                "email", "mail@mail",
                "password", "123456",
                "birthdate", "2002-10-10"
        );
        SignupResDto responseDto = new SignupResDto(
                "mail@mail",
                "123456"
        );
        given(userService.addUser(any())).willReturn(responseDto);


        RequestBuilder  request = MockMvcRequestBuilders
                .post("/auth-api/registration")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(map));
        MvcResult result =mvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("mail@mail"))
                .andReturn();


        assertThat(result.getResponse().getContentAsString())
                .isEqualTo(mapper.writeValueAsString(responseDto));
    }


    @Test
    void it_should_return_badRequest_when_request_is_on_bad_format()throws Exception{
        Map<String, String> map = Map.of(
                "email", "mail@mail",
                "password", "12345",
                "birthdate", "2002-10-10"
        );
        RequestBuilder  request = MockMvcRequestBuilders
                .post("/auth-api/registration")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(map));

        mvc.perform(request)
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    void it_should_return_badRequest_when_the_request_misses_one_value()throws Exception{
        Map<String, String> map = Map.of(
                "email", "mail@mail",
                "password", "123456"
        );
        RequestBuilder  request = MockMvcRequestBuilders
                .post("/auth-api/registration")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(map));

        mvc.perform(request)
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    void it_should_send_email_when_sendEmailForgetPassword_is_called() throws Exception{
        Map<String, String> map = Map.of(
                "email", "mail@mail"
        );

        RequestBuilder  request = MockMvcRequestBuilders
                .post("/auth-api/registration/forget-password")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(map));

        mvc.perform(request)
                .andExpect(status().isAccepted())
                .andReturn();
        verify(userService).sendForgetPasswordEmail("mail@mail");
    }

    @Test
    void it_should_return_bad_request_when_email_is_on_bad_format() throws Exception{
        Map<String, String> map = Map.of(
                "email", "mail"
        );

        RequestBuilder  request = MockMvcRequestBuilders
                .post("/auth-api/registration/forget-password")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(map));

        mvc.perform(request)
                .andExpect(status().isBadRequest())
                .andReturn();
        verify(userService, never()).sendForgetPasswordEmail("mail@mail");
    }

    @Test
    void it_should_call_service_when_resetPasswordUsingToken_is_called() throws Exception{
        Map<String, String> map = Map.of(
                "password", "123456",
                "token","token sample"
        );

        RequestBuilder  request = MockMvcRequestBuilders
                .post("/auth-api/registration/reset-password")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(map));

        mvc.perform(request)
                .andExpect(status().isCreated())
                .andReturn();
        verify(confirmationTokenService).resetPasswordUsingToken("token sample","123456");
    }

    @Test
    void it_should_not_call_service_when_resetPasswordUsingToken_is_called_with_bad_format() throws Exception{
        Map<String, String> map = Map.of(
                "password", "12345",
                "token","token sample"
        );

        RequestBuilder  request = MockMvcRequestBuilders
                .post("/auth-api/registration/reset-password")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(map));

        mvc.perform(request)
                .andExpect(status().isBadRequest())
                .andReturn();
        verify(confirmationTokenService, never()).resetPasswordUsingToken(any(),any());
    }
}