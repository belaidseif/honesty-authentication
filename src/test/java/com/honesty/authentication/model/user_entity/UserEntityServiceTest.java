package com.honesty.authentication.model.user_entity;

import com.honesty.authentication.controller.signup.dto.SignupReqDto;
import com.honesty.authentication.controller.signup.dto.SignupResDto;
import com.honesty.authentication.email.EmailSender;
import com.honesty.authentication.exception.ManagementException;
import com.honesty.authentication.exception.RegistrationException;
import com.honesty.authentication.model.authority.Authority;
import com.honesty.authentication.model.authority.AuthorityService;
import com.honesty.authentication.model.facebook.FacebookUser;
import com.honesty.authentication.model.google.CustomOAuth2User;
import com.honesty.authentication.model.token.ConfirmationToken;
import com.honesty.authentication.model.token.ConfirmationTokenService;
import com.honesty.authentication.user.ApplicationUserPermission;
import com.honesty.authentication.user.ApplicationUserRole;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.crypto.password.PasswordEncoder;


import java.time.Duration;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserEntityServiceTest {

    @Mock
    private PasswordEncoder encoder;
    @Mock
    AuthorityService authorityService;
    @Mock
    UserEntityRepository userRepo;
    @Mock
    ConfirmationTokenService confirmationTokenService;

    @Mock
    EmailSender emailSender;
    private UserEntityService underTest;

    @BeforeEach
    void setUp() {

        underTest = new UserEntityService(encoder, authorityService, userRepo, confirmationTokenService, emailSender);
    }

    @Test
    void it_should_add_user_when_there_is_no_one_has_the_same_email() {
        SignupReqDto user = new SignupReqDto(
                "mail@mail",
                "123456",
                LocalDate.of(2000,10,10),
                Gender.FEMALE
        );
        given(userRepo.existsByUsername(anyString())).willReturn(false);
        Set<Authority> set = new HashSet<>();
        set.add(new Authority(13L, ApplicationUserPermission.COMMENT_WRITE, ZonedDateTime.now()));
        given(authorityService.getAuthorities(any())).willReturn(set);

        SignupResDto response = underTest.addUser(user);
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
        assertThat(capturedUserEntity.getGender()).isEqualTo(Gender.FEMALE);
        assertThat(capturedUserEntity.getProvider()).isEqualTo(Provider.LOCAL);
        assertThat(capturedUserEntity.isVerified()).isFalse();

        assertThat(
                capturedUserEntity
                        .getAuthorities()
                        .iterator()
                        .next()
                        .getPermission()
        ).isEqualTo(ApplicationUserPermission.COMMENT_WRITE);
        assertThat(response.getEmail()).isEqualTo("mail@mail");
        assertThat(response.getPassword()).isEqualTo("123456");


    }

    @Test
    void itShouldThrowDuplicateExceptionWhenTheEmailExistsInTheDB(){
        SignupReqDto user = new SignupReqDto(
                "mail@mail",
                "123456",
                LocalDate.of(2000,10,10),
                Gender.MALE
        );
        given(userRepo.existsByUsername(anyString())).willReturn(true);
        assertThatThrownBy(() -> underTest.addUser(user))
                .isInstanceOf(RegistrationException.DuplicateUserException.class)
                .hasMessageContaining("email mail@mail is taken");

        verify(userRepo, never()).save(any());
    }

    @Test
    void it_should_only_get_user_with_facebook_when_it_exists(){
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername("username test");
        Optional<UserEntity> optionalUserEntity = Optional.of(userEntity);
        given(userRepo.findByFacebookId(15L)).willReturn(optionalUserEntity);

        FacebookUser facebookUser = new FacebookUser();
        facebookUser.setId("15");
        UserEntity user = underTest.saveUserWithFacebook(facebookUser);

        assertThat(user.getUsername()).isEqualTo("username test");

    }

    @Test
    void it_should_save_new_user_with_facebook_when_it_is_not_exist(){

        Optional<UserEntity> optionalUserEntity = Optional.empty();
        given(userRepo.findByFacebookId(25L)).willReturn(optionalUserEntity);



        FacebookUser facebookUser = new FacebookUser();
        facebookUser.setId("25");
        facebookUser.setBirthdate(LocalDate.now());
        UserEntity userEntity = underTest.saveUserWithFacebook(facebookUser);

        ArgumentCaptor<UserEntity> userEntityArgumentCaptor = ArgumentCaptor.forClass(UserEntity.class);
        verify(userRepo).save(userEntityArgumentCaptor.capture());
        UserEntity capturedUserEntity = userEntityArgumentCaptor.getValue();

        assertThat(capturedUserEntity.getUsername()).isEqualTo(null);
        assertThat(capturedUserEntity.getProvider()).isEqualTo(Provider.FACEBOOK);
        assertThat(capturedUserEntity.isVerified()).isTrue();
        assertThat(capturedUserEntity.getFacebookId()).isEqualTo(25);
        assertThat(capturedUserEntity.getUserRole()).isEqualTo(ApplicationUserRole.MEMBER);
        assertThat(capturedUserEntity.isEnabled()).isTrue();
    }

    @Test
    void it_should_only_get_user_with_google_when_it_exists(){
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername("username google test");
        Optional<UserEntity> optionalUserEntity = Optional.of(userEntity);
        given(userRepo.findByGoogleId("8")).willReturn(optionalUserEntity);

        CustomOAuth2User oauthUser  = mock(CustomOAuth2User.class);
        given(oauthUser.getId()).willReturn("8");
        UserEntity user = underTest.saveUserWithGoogle(oauthUser);

        assertThat(user.getUsername()).isEqualTo("username google test");
    }

    @Test
    void it_should_save_new_user_with_google_when_it_is_not_exist(){

        Optional<UserEntity> optionalUserEntity = Optional.empty();
        given(userRepo.findByGoogleId(any())).willReturn(optionalUserEntity);

        CustomOAuth2User oauthUser  = mock(CustomOAuth2User.class);
        given(oauthUser.getId()).willReturn("12");
        UserEntity user = underTest.saveUserWithGoogle(oauthUser);

        ArgumentCaptor<UserEntity> userEntityArgumentCaptor = ArgumentCaptor.forClass(UserEntity.class);
        verify(userRepo).save(userEntityArgumentCaptor.capture());
        UserEntity capturedUserEntity = userEntityArgumentCaptor.getValue();

        System.out.println(capturedUserEntity);
        assertThat(capturedUserEntity.getUsername()).isEqualTo(null);
        assertThat(capturedUserEntity.getProvider()).isEqualTo(Provider.GOOGLE);
        assertThat(capturedUserEntity.isVerified()).isTrue();
        assertThat(capturedUserEntity.getGoogleId()).isEqualTo("12");
        assertThat(capturedUserEntity.getUserRole()).isEqualTo(ApplicationUserRole.MEMBER);
        assertThat(capturedUserEntity.isEnabled()).isTrue();
    }

    @Test
    void it_should_send_new_token_when_its_called(){
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername("user name token");
        UUID uuid = UUID.randomUUID();
        given(userRepo.findById(uuid)).willReturn(Optional.of(userEntity));
        underTest.sendNewToken(uuid);

        ArgumentCaptor<ConfirmationToken> confirmationTokenArgumentCaptor = ArgumentCaptor.forClass(ConfirmationToken.class);
        verify(confirmationTokenService).saveConfirmationToken(confirmationTokenArgumentCaptor.capture());
        ConfirmationToken confirmationToken = confirmationTokenArgumentCaptor.getValue();


        assertThat(confirmationToken.getToken()).isNotNull();
        assertThat(confirmationToken.getUserEntity().getUsername()).isEqualTo("user name token");
        assertThat(Duration.between(confirmationToken.getCreateAt(), confirmationToken.getExpiresAt()).getSeconds()).isEqualTo(900);
    }

    @Test
    void it_should_send_confirmation_mail_when_sendForgetPasswordEmail_is_called(){
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername("user name token");
        given(userRepo.findByUsername("email")).willReturn(Optional.of(userEntity));

        underTest.sendForgetPasswordEmail("email");
        verify(confirmationTokenService).saveConfirmationToken(any());
    }

    @Test
    void it_should_throw_error_when_email_not_found(){
        given(userRepo.findByUsername("email")).willReturn(Optional.empty());

        assertThatThrownBy(()-> underTest.sendForgetPasswordEmail("email"))
                .isInstanceOf(RegistrationException.EmailNotFoundException.class)
                .hasMessageContaining("email not found");
        verify(confirmationTokenService, never()).saveConfirmationToken(any());
    }

    @Test
    void it_should_change_password_when_the_ancient_is_match(){
        UUID uuid = UUID.randomUUID();
        UserEntity userEntity = new UserEntity();
        userEntity.setPassword("123456");
        given(userRepo.findById(uuid)).willReturn(Optional.of(userEntity));

        given(encoder.matches("123456","123456")).willReturn(true);
        underTest.changePassword("123456","password", uuid);

        verify(userRepo).save(userEntity);
    }

    @Test
    void it_should_throw_error_when_the_ancient_password_does_not_match(){
        UUID uuid = UUID.randomUUID();
        UserEntity userEntity = new UserEntity();
        userEntity.setPassword("123456");
        given(userRepo.findById(uuid)).willReturn(Optional.of(userEntity));

        given(encoder.matches("password","123456")).willReturn(false);
        assertThatThrownBy(()-> underTest.changePassword("password","password", uuid))
                .isInstanceOf(ManagementException.PasswordDoesNotMatch.class)
                .hasMessageContaining("password doesn't match");


        verify(userRepo, never()).save(userEntity);
    }


    @Test
    @DisplayName("should return user with id")
    void test(){
        UUID userUid = UUID.randomUUID();
        UserEntity user = new UserEntity();
        user.setId(userUid);
        user.setUsername("username");
        given(userRepo.findById(userUid)).willReturn(Optional.of(user));

        UserEntity userInformations = underTest.getUserInformations(userUid);

        assertThat(userInformations.getId()).isEqualTo(userUid);
        assertThat(userInformations.getUsername()).isEqualTo("username");
    }
}