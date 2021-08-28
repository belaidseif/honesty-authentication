package com.honesty.authentication.model.authority;

import com.honesty.authentication.model.user_entity.UserEntity;
import com.honesty.authentication.user.ApplicationUserPermission;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Entity
@AllArgsConstructor
@Data
public class Authority {

    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator="seq_authority")
    private Long id;

    @Enumerated(EnumType.STRING)
    private ApplicationUserPermission permission;


    private ZonedDateTime notGrantedUntil;



    public Authority(ApplicationUserPermission permission, ZonedDateTime notGrantedUntil) {
        this.permission = permission;
        this.notGrantedUntil = notGrantedUntil;
    }

    public Authority() {
    }
}
