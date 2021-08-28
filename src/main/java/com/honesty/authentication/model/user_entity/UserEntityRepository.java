package com.honesty.authentication.model.user_entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserEntityRepository extends JpaRepository<UserEntity, UUID> {

    Optional<UserEntity> findByUsername(String username);

    Optional<UserEntity> findByFacebookId(Long id);

    Optional<UserEntity> findByGoogleId(String id);

    boolean existsByUsername(String username);

}
