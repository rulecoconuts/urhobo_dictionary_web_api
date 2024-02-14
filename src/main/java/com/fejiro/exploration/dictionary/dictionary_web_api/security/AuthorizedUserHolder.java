package com.fejiro.exploration.dictionary.dictionary_web_api.security;

import com.fejiro.exploration.dictionary.dictionary_web_api.service.user.AppUserDomainObject;

public interface AuthorizedUserHolder {
    Integer getId();

    AppUserDomainObject getUser();

    void setUser(AppUserDomainObject user);
}
