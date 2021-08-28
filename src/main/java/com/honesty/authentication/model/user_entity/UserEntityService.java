package com.honesty.authentication.model.user_entity;


import com.honesty.authentication.controller.signup.dto.SignupResponseDto;
import com.honesty.authentication.controller.signup.dto.UserSignupDto;
import com.honesty.authentication.exception.SignupException.DuplicateUserException;
import com.honesty.authentication.model.authority.AuthorityService;
import com.honesty.authentication.model.facebook.FacebookUser;
import com.honesty.authentication.model.google.CustomOAuth2User;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.util.Optional;

import static com.honesty.authentication.model.user_entity.Provider.*;



@Service
public class UserEntityService {
    private final PasswordEncoder encoder;
    private final AuthorityService authorityService;
    private final UserEntityRepository userRepo;

    public UserEntityService(PasswordEncoder encoder, AuthorityService authorityService, UserEntityRepository userRepo) {
        this.encoder = encoder;
        this.authorityService = authorityService;
        this.userRepo = userRepo;
    }

    public SignupResponseDto addUser(UserSignupDto userSignupDto){
        if(userRepo.existsByUsername(userSignupDto.getEmail()))
            throw new DuplicateUserException("email " + userSignupDto.getEmail() +" is taken");

        UserEntity user = new UserEntity();
        user.setUsername(userSignupDto.getEmail());
        user.setPassword(encoder.encode(userSignupDto.getPassword()));
        user.setBirthdate(userSignupDto.getBirthdate());
        user.createMember(authorityService);
        user.setProvider(LOCAL);
        userRepo.save(user);

        SignupResponseDto responseDto = new SignupResponseDto(
                userSignupDto.getEmail(),
                userSignupDto.getPassword()
        );
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
}
