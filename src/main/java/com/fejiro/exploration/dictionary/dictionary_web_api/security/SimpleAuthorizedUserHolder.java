package com.fejiro.exploration.dictionary.dictionary_web_api.security;

import org.springframework.stereotype.Component;

@Component
public class SimpleAuthorizedUserHolder implements AuthorizedUserHolder {
    
    @Override
    public Integer getId() {
        return null;
    }
}
