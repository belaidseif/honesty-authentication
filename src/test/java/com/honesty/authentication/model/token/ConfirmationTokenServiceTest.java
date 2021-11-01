package com.honesty.authentication.model.token;

import com.honesty.authentication.exception.ManagementException;
import com.honesty.authentication.model.user_entity.UserEntity;
import com.honesty.authentication.model.user_entity.UserEntityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConfirmationTokenServiceTest {

    @Mock
    private PasswordEncoder encoder;
    @Mock
    UserEntityRepository userRepo;
    @Mock
    ConfirmationTokenRepository confirmationRepo;

    private ConfirmationTokenService underTest;

    @BeforeEach
    void setUp() {

        underTest = new ConfirmationTokenService(confirmationRepo, userRepo, encoder);
    }

    @Test
    void saveConfirmationToken(){
        ConfirmationToken token = new ConfirmationToken( );
        token.setCreateAt(LocalDateTime.now());
        underTest.saveConfirmationToken(token);
        verify(confirmationRepo).save(token);
    }

    @Test
    void should_throw_token_not_found_when_token_is_not_found(){
        given(confirmationRepo.findByToken("token test")).willReturn(Optional.empty());

        assertThatThrownBy(()-> underTest.confirmToken("token test", UUID.randomUUID()))
                .isInstanceOf(ManagementException.TokenNotFound.class)
                .hasMessageContaining("token not found");
    }

    @Test
    void should_throw_expired_token_when_it_is(){
        ConfirmationToken token = new ConfirmationToken();
        token.setToken("token test");
        token.setExpiresAt(LocalDateTime.now().minusMinutes(1));
        given(confirmationRepo.findByToken("token test")).willReturn(Optional.of(token));

        assertThatThrownBy(()-> underTest.confirmToken("token test", UUID.randomUUID()))
                .isInstanceOf(ManagementException.TokenExpired.class)
                .hasMessageContaining("this token has been expired");
    }

    @Test
    void should_throw_not_same_user_when_user_id_is_wrong(){
        ConfirmationToken token = new ConfirmationToken();
        token.setToken("token test");
        token.setExpiresAt(LocalDateTime.now().plusMinutes(1));

        UserEntity userEntity = new UserEntity();
        userEntity.setId(UUID.randomUUID());
        token.setUserEntity(userEntity);
        given(confirmationRepo.findByToken("token test")).willReturn(Optional.of(token));

        assertThatThrownBy(()-> underTest.confirmToken("token test", UUID.randomUUID()))
                .isInstanceOf(ManagementException.NotSameUser.class)
                .hasMessageContaining("token is not compatible with this user");
    }

    @Test
    void should_set_verified_user_and_delete_token_when_arguments_are_correct(){
        UUID uuid = UUID.randomUUID();
        ConfirmationToken token = new ConfirmationToken();
        token.setToken("token test");
        token.setExpiresAt(LocalDateTime.now().plusMinutes(1));

        UserEntity userEntity = new UserEntity();
        userEntity.setId(uuid);
        token.setUserEntity(userEntity);
        given(confirmationRepo.findByToken("token test")).willReturn(Optional.of(token));

        underTest.confirmToken("token test", uuid);

        assertThat(token.getUserEntity().isVerified()).isTrue();
        verify(userRepo).save(token.getUserEntity());
        verify(confirmationRepo).delete(token);
    }

    @Test
    void should_throw_token_not_found_when_token_is_not_found_in_resetPassword_method(){
        given(confirmationRepo.findByToken("token test")).willReturn(Optional.empty());

        assertThatThrownBy(()-> underTest.resetPasswordUsingToken("token test", "123456"))
                .isInstanceOf(ManagementException.TokenNotFound.class)
                .hasMessageContaining("token not found");
    }

    @Test
    void should_throw_expired_token_when_it_is_in_resetPassword_method(){
        ConfirmationToken token = new ConfirmationToken();
        token.setToken("token test");
        token.setExpiresAt(LocalDateTime.now().minusMinutes(1));
        given(confirmationRepo.findByToken("token test")).willReturn(Optional.of(token));

        assertThatThrownBy(()-> underTest.resetPasswordUsingToken("token test", "123456"))
                .isInstanceOf(ManagementException.TokenExpired.class)
                .hasMessageContaining("this token has been expired");
    }

    @Test
    void should_reset_password_when_token_is_valid(){
        ConfirmationToken token = new ConfirmationToken();
        token.setToken("token test");
        token.setExpiresAt(LocalDateTime.now().plusMinutes(1));

        UserEntity userEntity = new UserEntity();
        userEntity.setPassword("password");
        token.setUserEntity(userEntity);
        given(confirmationRepo.findByToken("token test")).willReturn(Optional.of(token));

        underTest.resetPasswordUsingToken("token test", "123456");

        assertThat(token.getUserEntity().getPassword()).isNotEqualTo("password");
        verify(userRepo).save(token.getUserEntity());
        verify(confirmationRepo).delete(token);
    }
}