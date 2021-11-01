package com.honesty.authentication.model.user_entity;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.*;

@DataJpaTest
class UserEntityRepositoryTest {

    @Autowired
    UserEntityRepository userRepo;

    @AfterEach
    void tearDown(){
        userRepo.deleteAll();
    }

    @Test
    void existsByUsername() {
        assertThat(userRepo.existsByUsername("mail@mail")).isFalse();
        UserEntity user = new UserEntity();
        user.setUsername("mail@mail");
        userRepo.save(user);
        assertThat(userRepo.existsByUsername("mail@mail")).isTrue();
    }

    @Test
    void findByUserName(){
        assertThat(userRepo.findByUsername("user@user")).isEqualTo(Optional.empty());
        UserEntity user = new UserEntity();
        user.setUsername("user@user");
        userRepo.save(user);
        assertThat(userRepo.findByUsername("user@user").get()).isNotNull();
        assertThat(userRepo.findByUsername("user@user").get().getUsername()).isEqualTo("user@user");
    }

    @Test
    void findByFacebookId(){
        assertThat(userRepo.findByFacebookId(15L)).isEqualTo(Optional.empty());
        UserEntity user = new UserEntity();
        user.setUsername("face@user");
        user.setFacebookId(15L);
        userRepo.save(user);
        assertThat(userRepo.findByFacebookId(15L).get()).isNotNull();
        assertThat(userRepo.findByFacebookId(15L).get().getUsername()).isEqualTo("face@user");
    }

    @Test
    void findByGoogleId(){
        assertThat(userRepo.findByGoogleId("25")).isEqualTo(Optional.empty());
        UserEntity user = new UserEntity();
        user.setUsername("google@user");
        user.setFacebookId(15L);
        user.setGoogleId("25");
        userRepo.save(user);
        assertThat(userRepo.findByGoogleId("25").get()).isNotNull();
        assertThat(userRepo.findByGoogleId("25").get().getUsername()).isEqualTo("google@user");
    }

}