package com.fejiro.exploration.dictionary.dictionary_web_api.security;

import com.fejiro.exploration.dictionary.dictionary_web_api.service.user.AppUserDomainObject;
import com.fejiro.exploration.dictionary.dictionary_web_api.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var userData = userService.findByEmail(username);

        return userData.map(this::convertUserDomainDataToUserDetails)
                       .orElseThrow(
                               () -> new UsernameNotFoundException("Unable to find user with the supplied email"));
    }

    UserDetails convertUserDomainDataToUserDetails(AppUserDomainObject userDomainObject) {
        List<? extends GrantedAuthority> authorities = new ArrayList<>();
        return new User(userDomainObject.getEmail(), userDomainObject.getPassword(), authorities);
    }
}
