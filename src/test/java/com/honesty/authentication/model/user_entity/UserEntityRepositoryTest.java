package com.honesty.authentication.model.user_entity;

import com.honesty.authentication.exception.SignupException;
import com.honesty.authentication.model.authority.AuthorityService;
import com.honesty.authentication.user.ApplicationUserRole;
import org.h2.jdbc.JdbcSQLIntegrityConstraintViolationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.validation.ConstraintViolationException;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.HashSet;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserEntityRepositoryTest {

    @Autowired
    UserEntityRepository userRepo;

    @AfterEach
    void tearDown(){
        userRepo.deleteAll();
    }
//    @Test
//    void existsByUsername() {
//        assertThat(userRepo.existsByUsername("mail@mail")).isFalse();
//        UserEntity user = new UserEntity(
//                "mail@mail",
//                "123456",
//                ZonedDateTime.now(),
//                null,
//                LocalDate.now(),
//                ApplicationUserRole.MEMBER,
//                true, true, true, true,
//                new HashSet<>()
//        );
//        userRepo.save(user);
//        assertThat(userRepo.existsByUsername("mail@mail")).isTrue();
//    }

}