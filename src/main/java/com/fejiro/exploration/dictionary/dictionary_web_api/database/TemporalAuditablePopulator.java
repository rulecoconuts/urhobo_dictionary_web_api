package com.fejiro.exploration.dictionary.dictionary_web_api.database;

public interface TemporalAuditablePopulator {
    void populateForCreation(TemporalAuditable model);

    void populateForUpdate(TemporalAuditable model);
}
