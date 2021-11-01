package com.honesty.authentication.model.token;

import com.honesty.authentication.model.user_entity.UserEntity;
import com.honesty.authentication.model.user_entity.UserEntityRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.*;

@DataJpaTest
class ConfirmationTokenRepositoryTest {

    @Autowired
    ConfirmationTokenRepository underTest;

    @Autowired
    UserEntityRepository userEntityRepository;

    @AfterEach
    void tearDown(){
        underTest.deleteAll();
    }

    @Test
    void findByToken(){
        assertThat(underTest.findByToken("token test")).isEqualTo(Optional.empty());

        UserEntity userEntity = new UserEntity();
        userEntityRepository.save(userEntity);
        ConfirmationToken token = new ConfirmationToken();
        token.setToken("token test");
        token.setCreateAt(LocalDateTime.now());
        token.setUserEntity(userEntity);

        underTest.save(token);
        assertThat(underTest.findByToken("token test").get()).isEqualTo(token);

    }
}