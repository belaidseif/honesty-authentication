package com.honesty.authentication.model.token;

import com.honesty.authentication.model.user_entity.UserEntityRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static com.honesty.authentication.exception.ManagementException.*;

@Service
@AllArgsConstructor
public class ConfirmationTokenService {

    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final UserEntityRepository userEntityRepository;
    private final PasswordEncoder passwordEncoder;


    public void saveConfirmationToken(ConfirmationToken token){
        confirmationTokenRepository.save(token);
    }

    public void confirmToken(String token, UUID userUid) {
        Optional<ConfirmationToken> byToken = confirmationTokenRepository.findByToken(token);
        ConfirmationToken confirmationToken = byToken.orElseThrow(() -> new TokenNotFound("token not found"));

        if(confirmationToken.getExpiresAt().isBefore(LocalDateTime.now()))
            throw new TokenExpired("this token has been expired");

        if(!(userUid).equals(confirmationToken.getUserEntity().getId()))
            throw new NotSameUser("token is not compatible with this user");

        confirmationToken.getUserEntity().setVerified(true);
        userEntityRepository.save(confirmationToken.getUserEntity());
        confirmationTokenRepository.delete(confirmationToken);
    }

    public void resetPasswordUsingToken(String token, String password) {
        Optional<ConfirmationToken> byToken = confirmationTokenRepository.findByToken(token);
        ConfirmationToken confirmationToken = byToken.orElseThrow(() -> new TokenNotFound("token not found"));

        if(confirmationToken.getExpiresAt().isBefore(LocalDateTime.now()))
            throw new TokenExpired("this token has been expired");

        confirmationToken.getUserEntity().setPassword(passwordEncoder.encode(password));
        userEntityRepository.save(confirmationToken.getUserEntity());
        confirmationTokenRepository.delete(confirmationToken);
    }
}
