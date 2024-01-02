package com.fejiro.exploration.dictionary.dictionary_web_api.database;

import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Component
public class SimpleTemporalAuditablePopulator implements TemporalAuditablePopulator {
    @Override
    public void populateForCreation(TemporalAuditable model) {
        var now = OffsetDateTime.now();
        model.setCreatedAt(now);
        model.setUpdatedAt(now);
    }

    @Override
    public void populateForUpdate(TemporalAuditable model) {
        var now = OffsetDateTime.now();
        model.setUpdatedAt(now);
    }
}
