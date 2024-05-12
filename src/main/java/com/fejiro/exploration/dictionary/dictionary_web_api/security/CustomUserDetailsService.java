package com.fejiro.exploration.dictionary.dictionary_web_api.security;

import com.fejiro.exploration.dictionary.dictionary_web_api.service.user.AppUserDomainObject;
import com.fejiro.exploration.dictionary.dictionary_web_api.service.user.UserService;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        boolean shouldFindByEmail = EmailValidator.getInstance().isValid(username);
        Optional<AppUserDomainObject> userData;
        if (shouldFindByEmail) {
            
            userData = userService.findByEmail(username);
        } else {

            userData = userService.findByUsername(username);
        }

        return userData.map((user) -> convertUserDomainDataToUserDetails(user, username))
                       .orElseThrow(
                               () -> new UsernameNotFoundException("Unable to find user with the supplied email"));
    }

    UserDetails convertUserDomainDataToUserDetails(AppUserDomainObject userDomainObject, String username) {
        List<? extends GrantedAuthority> authorities = new ArrayList<>();
        return new CustomSecurityUserDetails(username, userDomainObject.getPassword(), authorities,
                                             userDomainObject);
    }
}
