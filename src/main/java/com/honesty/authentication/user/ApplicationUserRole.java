package com.honesty.authentication.user;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static com.honesty.authentication.user.ApplicationUserPermission.*;

public enum ApplicationUserRole {
    MEMBER(new HashSet<>(Arrays.asList(POST_WRITE, COMMENT_WRITE, MESSAGE_WRITE))),
    ADMIN(new HashSet<>(Arrays.asList(POST_WRITE, COMMENT_WRITE, MESSAGE_WRITE, USER_EDIT,SYSTEM_CONFIG)));

    private final Set<ApplicationUserPermission> permissions;

    ApplicationUserRole(Set<ApplicationUserPermission> permissions) {
        this.permissions = permissions;
    }

    public Set<ApplicationUserPermission> getPermissions(){
        return permissions;
    }

    public Set<SimpleGrantedAuthority> getGrantedAuthorities(){
        Set<SimpleGrantedAuthority> permissions = getPermissions().stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
                .collect(Collectors.toSet());
        permissions.add(new SimpleGrantedAuthority("ROLE_"+this.name()));
        return permissions;
    }
}
