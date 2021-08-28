package com.honesty.authentication.security;

import com.honesty.authentication.jwt.JwtConfig;
import com.honesty.authentication.jwt.JwtUsernameAndPasswordAuthenticationFilter;
import com.honesty.authentication.model.google.CustomOAuth2User;
import com.honesty.authentication.model.google.CustomOAuth2UserService;
import com.honesty.authentication.model.user_entity.UserEntity;
import com.honesty.authentication.model.user_entity.UserEntityService;
import com.honesty.authentication.user.ApplicationUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class ApplicationSecurityConfig extends WebSecurityConfigurerAdapter {

    private final PasswordEncoder passwordEncoder;
    private final ApplicationUserService userService;
    private final JwtConfig jwtConfig;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final UserEntityService userEntityService;
    @Autowired
    public ApplicationSecurityConfig(PasswordEncoder passwordEncoder, ApplicationUserService userService, JwtConfig jwtConfig, CustomOAuth2UserService customOAuth2UserService, UserEntityService userEntityService) {
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
        this.jwtConfig = jwtConfig;
        this.customOAuth2UserService = customOAuth2UserService;
        this.userEntityService = userEntityService;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception{

        http
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilter(new JwtUsernameAndPasswordAuthenticationFilter(authenticationManager(), jwtConfig))
                .authorizeRequests()
                .antMatchers("/auth-api/signup").permitAll()
                .antMatchers("/auth-api/facebook/**").permitAll()
                .antMatchers("/oauth/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .oauth2Login()
                .userInfoEndpoint()
                .userService(customOAuth2UserService)
                .and()
                .successHandler(new AuthenticationSuccessHandler() {
                    @Override
                    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
                        CustomOAuth2User oauthUser = (CustomOAuth2User) authentication.getPrincipal();
                        UserEntity userEntity = userEntityService.saveUserWithGoogle(oauthUser);

                        UserDetails userDetails = userService.getUserDetailsFromUserEntity(userEntity);
                        String accessToken = jwtConfig.getAccessToken(userDetails);

                        String refreshToken = jwtConfig.getRefreshToken(userDetails);

                        httpServletResponse.addHeader("accessToken", jwtConfig.getTokenPrefix() + accessToken);
                        httpServletResponse.addHeader("refreshToken", refreshToken);
                    }
                });
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception{
        auth.authenticationProvider(daoAuthenticationProvider());
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder);
        provider.setUserDetailsService(userService);
        return provider;
    }
}
