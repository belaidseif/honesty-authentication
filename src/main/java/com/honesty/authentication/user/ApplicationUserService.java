package com.honesty.authentication.user;


import com.honesty.authentication.model.user_entity.UserEntity;
import com.honesty.authentication.model.user_entity.UserEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
public class ApplicationUserService implements UserDetailsService {

    @Autowired private UserEntityRepository userRepo;



    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {

        Optional<UserEntity> optionalUserEntity = userRepo.findByUsername(s);
        UserEntity userEntity = optionalUserEntity
                .orElseThrow(()-> new UsernameNotFoundException("user not found with this address mail: " + s));

    return getUserDetailsFromUserEntity(userEntity);
    }


    public UserDetails getUserDetailsFromUserEntity(UserEntity userEntity) {
        Set<SimpleGrantedAuthority> grantedAuthorities = userEntity.getAuthorities().stream()
                .filter(authority -> authority.getNotGrantedUntil().isBefore(ZonedDateTime.now()))
                .map(authority -> new SimpleGrantedAuthority(authority.getPermission().getPermission()))
                .collect(Collectors.toSet());
        grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_"+userEntity.getUserRole().name()));

        UserDetails userDetails = new ApplicationUser(
                userEntity.getId().toString(),
                userEntity.getPassword(),
                grantedAuthorities,
                userEntity.isAccountNonExpired(),
                userEntity.isAccountNonLocked(),
                userEntity.isCredentialsNonExpired(),
                userEntity.isEnabled()
        );
        return userDetails;
    }

    public UserDetails getUserDetailsById(String id){
        Optional<UserEntity> optionalUserEntity = userRepo.findById(UUID.fromString(id));
        UserEntity userEntity = optionalUserEntity
                .orElseThrow(()-> new UsernameNotFoundException("user not found with this address mail: " + id));

        return getUserDetailsFromUserEntity(userEntity);
    }
}
