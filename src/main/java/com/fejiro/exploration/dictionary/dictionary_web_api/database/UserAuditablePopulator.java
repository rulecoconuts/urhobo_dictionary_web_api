package com.fejiro.exploration.dictionary.dictionary_web_api.database;

public interface UserAuditablePopulator {
    void populateForCreation(UserAuditable model);

    void populateForUpdate(UserAuditable model);
}
