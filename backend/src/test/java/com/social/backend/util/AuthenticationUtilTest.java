package com.social.backend.util;

import java.util.Collections;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import com.social.backend.config.IdentifiedUserDetails;

public class AuthenticationUtilTest {
  
  @Test
  public void getPrincipal_exception_onNullAuthentication() {
    Assertions
        .assertThatThrownBy(() -> AuthenticationUtil.getPrincipal(null))
        .isExactlyInstanceOf(IllegalArgumentException.class);
  }
  
  @Test
  public void getPrincipal_null_onNullPrincipal() {
    Authentication authentication = new TestingAuthenticationToken(null, null);
    
    Assertions
        .assertThat(AuthenticationUtil.getPrincipal(authentication))
        .isNull();
  }
  
  @Test
  public void getPrincipal_null_whenNotExpectedPrincipalType() {
    UserDetails principal = new User(
        "username",
        "password",
        Collections.emptySet()
    );
    Authentication authentication = new TestingAuthenticationToken(principal, null);
    
    Assertions
        .assertThat(AuthenticationUtil.getPrincipal(authentication))
        .isNull();
  }
  
  @Test
  public void getPrincipal() {
    UserDetails principal = new IdentifiedUserDetails(
        1L,
        "username",
        "password",
        Collections.emptySet()
    );
    Authentication authentication = new TestingAuthenticationToken(principal, null);
    
    Assertions
        .assertThat(AuthenticationUtil.getPrincipal(authentication))
        .isExactlyInstanceOf(IdentifiedUserDetails.class)
        .isEqualToComparingFieldByField(new IdentifiedUserDetails(
            1L,
            "username",
            "password",
            Collections.emptySet()
        ));
  }
  
}
