package com.fejiro.exploration.dictionary.dictionary_web_api.config.service.user;

import com.fejiro.exploration.dictionary.dictionary_web_api.database.CRUDDAO;
import com.fejiro.exploration.dictionary.dictionary_web_api.database.ConfigurableGenericJOOQCRUDDAO;
import com.fejiro.exploration.dictionary.dictionary_web_api.database.SimpleGeneralAuditablePopulator;
import com.fejiro.exploration.dictionary.dictionary_web_api.tables.AppUser;
import com.fejiro.exploration.dictionary.dictionary_web_api.tables.records.AppUserRecord;
import com.fejiro.exploration.dictionary.dictionary_web_api.service.user.AppUserDataObject;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.util.stream.StreamSupport;

@Configuration
public class UserDataModelConfig {

    @Autowired
    DSLContext dsl;

    @Bean
    @Lazy
    public CRUDDAO<AppUserDataObject, Integer> userCRUDDAO(SimpleGeneralAuditablePopulator auditablePopulator) {
        return ConfigurableGenericJOOQCRUDDAO
                .<AppUserDataObject, Integer, AppUserRecord>builder()
                .modelClass(AppUserDataObject.class)
                .dslContext(dsl)
                .table(AppUser.APP_USER)
                .updatePreProcessFunction(user -> {
                    auditablePopulator.populateForUpdate(user);
                    return user;
                })
                .creationPreProcessFunction(user -> {
                    auditablePopulator.populateForCreation(user);
                    return user;
                })
                .idExtractionFunction(AppUserDataObject::getId)
                .idCollectionMatchConditionGenerator(ids -> {
                    return AppUser.APP_USER.ID.in(StreamSupport.stream(ids.spliterator(), false).toList());
                })
                .idMatchConditionGenerator(AppUser.APP_USER.ID::eq)
                .validateForCreationFunction(user -> {
                    if (user.getEmail() == null || user.getEmail().isBlank()) {
                        return "Email is empty";
                    }

                    if (user.getId() != null) return "User to be created has a non-null id";

                    if (user.getUsername() != null && user.getUsername().isBlank())
                        return "UserName is blank";
                    return null;
                })
                .validateForUpdateFunction(user -> {
                    if (user.getEmail() == null || user.getEmail().isBlank()) {
                        return "Email is empty";
                    }

                    if (user.getId() == null) return "User to be updated has a null id";

                    if (user.getUsername() == null || user.getUsername().isBlank())
                        return "UserName is blank";
                    return null;
                })
                .build();
    }
}
