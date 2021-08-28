package com.honesty.authentication.model.user_entity;


import com.honesty.authentication.model.authority.Authority;
import com.honesty.authentication.model.authority.AuthorityService;
import com.honesty.authentication.user.ApplicationUserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Set;
import java.util.UUID;

import static com.honesty.authentication.user.ApplicationUserRole.*;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    private UUID id;
    @Column(unique = true)
    private String username;
    private String password;
    private ZonedDateTime createdAt;

    @Column(unique = true)
    private Long facebookId;

    @Column(unique = true)
    private String googleId;

    private LocalDate birthdate;

    @Enumerated(EnumType.STRING)
    private ApplicationUserRole userRole;
    private boolean isAccountNonExpired;
    private boolean isAccountNonLocked;
    private boolean isCredentialsNonExpired;
    private boolean isEnabled;

    @Enumerated(EnumType.STRING)
    private Provider provider;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_entity_id")
    private Set<Authority> authorities;




    public void createMember(AuthorityService authorityService) {
        isAccountNonExpired = true;
        isAccountNonLocked = true;
        isCredentialsNonExpired = true;
        isEnabled = true;
        authorities = authorityService.getAuthorities(MEMBER);
        userRole = MEMBER;
        createdAt = ZonedDateTime.now();
    }
}
