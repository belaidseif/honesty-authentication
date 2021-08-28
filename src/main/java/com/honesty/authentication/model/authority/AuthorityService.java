package com.honesty.authentication.model.authority;

import com.honesty.authentication.user.ApplicationUserPermission;
import com.honesty.authentication.user.ApplicationUserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthorityService {

    @Autowired
    AuthorityRepository authorityRepo;

    public Set<Authority> getAuthorities(ApplicationUserRole role){
        return role.getGrantedAuthorities().stream()
                .map(a -> new Authority(
                            ApplicationUserPermission.getPermissionFromString(a.getAuthority()),
                            ZonedDateTime.now()
                    )
                 )
                .filter(authority -> authority.getPermission() != null)
                .map(authority -> authorityRepo.save(authority))
                .collect(Collectors.toSet());
    }
}
