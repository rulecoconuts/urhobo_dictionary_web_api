package com.fejiro.exploration.dictionary.dictionary_web_api.config.security;

import com.fejiro.exploration.dictionary.dictionary_web_api.security.SimpleAuthorizedUserHolder;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.context.annotation.RequestScope;

@Configurable
public class UserHolderConfig {

    @Bean
    @Lazy
    @RequestScope
    public SimpleAuthorizedUserHolder simpleAuthorizedUserHolder() {
        return new SimpleAuthorizedUserHolder();
    }
}
