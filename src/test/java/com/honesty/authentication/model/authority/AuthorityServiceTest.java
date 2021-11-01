package com.honesty.authentication.model.authority;


import com.honesty.authentication.user.ApplicationUserPermission;
import com.honesty.authentication.user.ApplicationUserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AuthorityServiceTest {

    @Mock
    AuthorityRepository authorityRepo;

    private AuthorityService underTest;

    @BeforeEach
    void setUp() {

        underTest = new AuthorityService();
        underTest.authorityRepo = authorityRepo;
    }

    @Test
    void should_save_all_of_authorities(){

        underTest.getAuthorities(ApplicationUserRole.MEMBER);

        ArgumentCaptor<Authority> authorityCaptor = ArgumentCaptor.forClass(Authority.class);
        verify(authorityRepo, times(3)).save(authorityCaptor.capture());
        List<Authority> allValues = authorityCaptor.getAllValues();


        assertThat(allValues.get(0).getPermission()).isEqualTo(ApplicationUserPermission.COMMENT_WRITE);
        assertThat(allValues.get(1).getPermission()).isEqualTo(ApplicationUserPermission.POST_WRITE);
        assertThat(allValues.get(2).getPermission()).isEqualTo(ApplicationUserPermission.MESSAGE_WRITE);
    }

    @Test
    void should_return_all_authorities(){
        Authority authority = new Authority();
        authority.setId(15L);
        given(authorityRepo.save(any())).willReturn(authority);

        Set<Authority> authorities = underTest.getAuthorities(ApplicationUserRole.MEMBER);

        assertThat(authorities.iterator().next().getId()).isEqualTo(15L);

    }
}