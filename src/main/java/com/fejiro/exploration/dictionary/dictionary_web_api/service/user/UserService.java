package com.fejiro.exploration.dictionary.dictionary_web_api.service.user;

import com.fejiro.exploration.dictionary.dictionary_web_api.service.CRUDService;

import java.util.Optional;

public interface UserService extends CRUDService<AppUserDomainObject, Integer> {
    Optional<AppUserDomainObject> findByUsername(String username);

    Optional<AppUserDomainObject> findByEmail(String email);
}
