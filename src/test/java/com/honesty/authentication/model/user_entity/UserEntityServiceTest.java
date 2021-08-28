package com.honesty.authentication.model.user_entity;

import com.honesty.authentication.controller.signup.dto.UserSignupDto;
import com.honesty.authentication.exception.SignupException;
import com.honesty.authentication.model.authority.AuthorityService;
import com.honesty.authentication.user.ApplicationUserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.ZonedDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserEntityServiceTest {

    @Mock
    private PasswordEncoder encoder;
    @Mock
    AuthorityService authorityService;
    @Mock
    UserEntityRepository userRepo;
    private UserEntityService underTest;

    @BeforeEach
    void setUp() {
        underTest = new UserEntityService(encoder, authorityService, userRepo);
    }

    @Test
    void it_should_add_user_when_there_is_no_one_has_the_same_email() {
        UserSignupDto user = new UserSignupDto(
                "mail@mail",
                "123456",
                LocalDate.of(2000,10,10)
        );
        given(userRepo.existsByUsername(anyString())).willReturn(false);
        underTest.addUser(user);
        ArgumentCaptor<UserEntity> userEntityArgumentCaptor = ArgumentCaptor.forClass(UserEntity.class);
        verify(userRepo).save(userEntityArgumentCaptor.capture());
        UserEntity capturedUserEntity = userEntityArgumentCaptor.getValue();


        assertThat(capturedUserEntity.getBirthdate()).isEqualTo(LocalDate.of(2000,10,10));
        assertThat(capturedUserEntity.getUsername()).isEqualTo("mail@mail");
        assertThat(capturedUserEntity.getCreatedAt()).isNotNull();
        assertThat(capturedUserEntity.isAccountNonExpired()).isTrue();
        assertThat(capturedUserEntity.isAccountNonLocked()).isTrue();
        assertThat(capturedUserEntity.isCredentialsNonExpired()).isTrue();
        assertThat(capturedUserEntity.isEnabled()).isTrue();


    }

    @Test
    void itShouldThrowDuplicateExceptionWhenTheEmailExistsInTheDB(){
        UserSignupDto user = new UserSignupDto(
                "mail@mail",
                "123456",
                LocalDate.of(2000,10,10)
        );
        given(userRepo.existsByUsername(anyString())).willReturn(true);
        assertThatThrownBy(() -> underTest.addUser(user))
                .isInstanceOf(SignupException.DuplicateUserException.class)
                .hasMessageContaining("email mail@mail is taken");

        verify(userRepo, never()).save(any());
    }
}