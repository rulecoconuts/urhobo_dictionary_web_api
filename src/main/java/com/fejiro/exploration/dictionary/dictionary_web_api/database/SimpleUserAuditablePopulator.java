package com.fejiro.exploration.dictionary.dictionary_web_api.database;

import com.fejiro.exploration.dictionary.dictionary_web_api.security.AuthorizedUserHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SimpleUserAuditablePopulator implements UserAuditablePopulator {
    @Autowired
    AuthorizedUserHolder authorizedUserHolder;

    @Override
    public void populateForCreation(UserAuditable model) {
        var id = authorizedUserHolder.getId();
        model.setCreatedBy(id);
        model.setUpdatedBy(id);
    }

    @Override
    public void populateForUpdate(UserAuditable model) {
        var id = authorizedUserHolder.getId();
        model.setUpdatedBy(id);
    }
}
