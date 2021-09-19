package com.honesty.authentication.model.user_entity;


import com.honesty.authentication.controller.signup.dto.SignupResDto;
import com.honesty.authentication.controller.signup.dto.SignupReqDto;
import com.honesty.authentication.email.EmailSender;
import com.honesty.authentication.exception.ManagementException.*;
import com.honesty.authentication.exception.RegistrationException.*;
import com.honesty.authentication.model.authority.AuthorityService;
import com.honesty.authentication.model.facebook.FacebookUser;
import com.honesty.authentication.model.google.CustomOAuth2User;

import com.honesty.authentication.model.token.ConfirmationToken;
import com.honesty.authentication.model.token.ConfirmationTokenService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;



import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.honesty.authentication.model.user_entity.Provider.*;



@Service
public class UserEntityService {

    private final PasswordEncoder encoder;
    private final AuthorityService authorityService;
    private final UserEntityRepository userRepo;
    private final ConfirmationTokenService confirmationTokenService;
    private final EmailSender emailSender;



    public UserEntityService(PasswordEncoder encoder, AuthorityService authorityService, UserEntityRepository userRepo, ConfirmationTokenService confirmationTokenService, EmailSender emailSender) {
        this.encoder = encoder;
        this.authorityService = authorityService;
        this.userRepo = userRepo;
        this.confirmationTokenService = confirmationTokenService;
        this.emailSender = emailSender;
    }

    public SignupResDto addUser(SignupReqDto signupReqDto){
        if(userRepo.existsByUsername(signupReqDto.getEmail()))
            throw new DuplicateUserException("email " + signupReqDto.getEmail() +" is taken");

        UserEntity user = new UserEntity();
        user.setUsername(signupReqDto.getEmail());
        user.setPassword(encoder.encode(signupReqDto.getPassword()));
        user.setBirthdate(signupReqDto.getBirthdate());
        user.createMember(authorityService);
        user.setProvider(LOCAL);
        user.setVerified(false);
        userRepo.save(user);

        SignupResDto responseDto = new SignupResDto(
                signupReqDto.getEmail(),
                signupReqDto.getPassword()
        );
        sendConfirmationToken(user);
        return responseDto;
    }

    public UserEntity saveUserWithFacebook(FacebookUser facebookUser) {
        Optional<UserEntity> userByFacebookId = userRepo.findByFacebookId(Long.parseLong(facebookUser.getId()));
        if(userByFacebookId.isPresent()){
            return userByFacebookId.get();
        }
        else{
            return addUserWithFacebook(facebookUser);
        }
    }

    private UserEntity addUserWithFacebook(FacebookUser facebookUser) {
        UserEntity userEntity = new UserEntity();
        userEntity.setFacebookId(Long.parseLong(facebookUser.getId()));
        userEntity.setBirthdate(facebookUser.getBirthdate());
        userEntity.createMember(authorityService);
        userEntity.setProvider(FACEBOOK);
        return userRepo.save(userEntity);
    }

    public UserEntity saveUserWithGoogle(CustomOAuth2User oauthUser) {
        Optional<UserEntity> userByGoogleId = userRepo.findByGoogleId(oauthUser.getId());
        if(userByGoogleId.isPresent()){
            return userByGoogleId.get();
        }
        else{
            return addUserWithGoogle(oauthUser);
        }

    }

    private UserEntity addUserWithGoogle(CustomOAuth2User oauthUser) {
        UserEntity userEntity = new UserEntity();

        userEntity.setGoogleId(oauthUser.getId());
        userEntity.createMember(authorityService);
        userEntity.setProvider(GOOGLE);
        return userRepo.save(userEntity);
    }

    private void sendConfirmationToken(UserEntity userEntity){
        //        Coupled method
        String token = UUID.randomUUID().toString();
        ConfirmationToken confirmationToken = new ConfirmationToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15),
                userEntity
        );
        confirmationTokenService.saveConfirmationToken(confirmationToken);
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(()-> emailSender.send("belaidseif@gmail.com", confirmationToken.getToken()));
    }

    public void sendNewToken(UUID userUid) {
        UserEntity userEntity = userRepo.findById(userUid).get();
        sendConfirmationToken(userEntity);
    }

    public void sendForgetPasswordEmail(String email) {
        Optional<UserEntity> byUsername = userRepo.findByUsername(email);
        UserEntity userEntity = byUsername.orElseThrow(() -> new EmailNotFoundException("email not found"));
        sendConfirmationToken(userEntity);
    }


    public void changePassword(String ancientPassword, String newPassword, UUID userUid) {
        UserEntity userEntity = userRepo.findById(userUid).get();
        if(!encoder.matches(ancientPassword, userEntity.getPassword()))
            throw new PasswordDoesNotMatch("password doesn't match");

        userEntity.setPassword(encoder.encode(newPassword));
        userRepo.save(userEntity);
    }
}
