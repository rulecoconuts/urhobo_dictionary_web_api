package com.fejiro.exploration.dictionary.dictionary_web_api.database;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SimpleGeneralAuditablePopulator {
    @Autowired
    TemporalAuditablePopulator temporalAuditablePopulator;

    @Autowired
    UserAuditablePopulator userAuditablePopulator;

    public <T> void populateForCreation(T model) {
        if (model instanceof TemporalAuditable) {
            temporalAuditablePopulator.populateForCreation((TemporalAuditable) model);
        }

        if (model instanceof UserAuditable) {
            userAuditablePopulator.populateForCreation((UserAuditable) model);
        }
    }

    public <T> void populateForUpdate(T model) {
        if (model instanceof TemporalAuditable) {
            temporalAuditablePopulator.populateForUpdate((TemporalAuditable) model);
        }

        if (model instanceof UserAuditable) {
            userAuditablePopulator.populateForUpdate((UserAuditable) model);
        }
    }
}
