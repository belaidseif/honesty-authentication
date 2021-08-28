package com.honesty.authentication.model.facebook;

import com.honesty.authentication.model.user_entity.UserEntity;
import com.honesty.authentication.model.user_entity.UserEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.impl.FacebookTemplate;
import org.springframework.social.facebook.connect.FacebookConnectionFactory;
import org.springframework.social.oauth2.OAuth2Parameters;
import org.springframework.stereotype.Service;

@Service
public class FacebookService {
    private String accessToken;

    @Value("${spring.social.facebook.app-id}")
    private String facebookAppId;

    @Value("${spring.social.facebook.app-secret}")
    private String facebookSecret;

    @Value("${application.environment}")
    private String environment;

    @Autowired
    UserEntityService userEntityService;
    private FacebookConnectionFactory createConnection(){
        return new FacebookConnectionFactory(facebookAppId, facebookSecret);
    }


    public String generateFacebookAuthorizeUrl() {
        OAuth2Parameters params = new OAuth2Parameters();
        params.setRedirectUri(environment + "/auth-api/facebook");
        params.setScope("email");
        return createConnection().getOAuthOperations().buildAuthenticateUrl(params);
    }

    public UserEntity generateFacebookAccessToken(String code) {
        accessToken = createConnection().getOAuthOperations().exchangeForAccess(
                code,
                environment + "/auth-api/facebook",
                null
        ).getAccessToken();

        Facebook facebook = new FacebookTemplate(accessToken);
        String[] fields = {"id", "first_name", "hometown"};
        FacebookUser user = facebook.fetchObject("me", FacebookUser.class, fields);

        return userEntityService.saveUserWithFacebook(user);
    }


}
