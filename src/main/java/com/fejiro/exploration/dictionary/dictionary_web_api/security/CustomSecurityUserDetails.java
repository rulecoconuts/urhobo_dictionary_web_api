package com.fejiro.exploration.dictionary.dictionary_web_api.security;

import com.fejiro.exploration.dictionary.dictionary_web_api.service.user.AppUserDomainObject;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Getter
public class CustomSecurityUserDetails extends User {
    AppUserDomainObject userDomainObject;

    public CustomSecurityUserDetails(String username, String password,
                                     Collection<? extends GrantedAuthority> authorities,
                                     AppUserDomainObject userDomainObject) {
        super(username, password, authorities);
        this.userDomainObject = userDomainObject;
    }
}
