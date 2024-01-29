package com.fejiro.exploration.dictionary.dictionary_web_api.security;

import com.fejiro.exploration.dictionary.dictionary_web_api.service.user.AppUserDomainObject;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Data
@NoArgsConstructor
public class SimpleAuthorizedUserHolder implements AuthorizedUserHolder {

    AppUserDomainObject user;

    @Override
    public Integer getId() {
        return null;
    }
}
