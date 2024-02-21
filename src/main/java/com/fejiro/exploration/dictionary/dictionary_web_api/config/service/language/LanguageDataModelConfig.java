package com.fejiro.exploration.dictionary.dictionary_web_api.config.service.language;

import com.fejiro.exploration.dictionary.dictionary_web_api.database.CRUDDAO;
import com.fejiro.exploration.dictionary.dictionary_web_api.database.ConfigurableGenericJOOQCRUDDAO;
import com.fejiro.exploration.dictionary.dictionary_web_api.database.SimpleGeneralAuditablePopulator;
import com.fejiro.exploration.dictionary.dictionary_web_api.service.language.LanguageDataObject;
import com.fejiro.exploration.dictionary.dictionary_web_api.service.user.AppUserDataObject;
import com.fejiro.exploration.dictionary.dictionary_web_api.tables.AppUser;
import com.fejiro.exploration.dictionary.dictionary_web_api.tables.Language;
import com.fejiro.exploration.dictionary.dictionary_web_api.tables.records.AppUserRecord;
import com.fejiro.exploration.dictionary.dictionary_web_api.tables.records.LanguageRecord;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.util.stream.StreamSupport;

@Configuration
public class LanguageDataModelConfig {

    @Autowired
    DSLContext dsl;

    @Bean
    @Lazy
    CRUDDAO<LanguageDataObject, Integer> languageCRUDDAO(SimpleGeneralAuditablePopulator auditablePopulator) {
        return ConfigurableGenericJOOQCRUDDAO
                .<LanguageDataObject, Integer, LanguageRecord>builder()
                .modelClass(LanguageDataObject.class)
                .dslContext(dsl)
                .table(Language.LANGUAGE)
                .updatePreProcessFunction(language -> {
                    auditablePopulator.populateForUpdate(language);
                    return language;
                })
                .creationPreProcessFunction(language -> {
                    auditablePopulator.populateForCreation(language);
                    return language;
                })
                .idExtractionFunction(LanguageDataObject::getId)
                .idCollectionMatchConditionGenerator(ids -> {
                    return Language.LANGUAGE.ID.in(StreamSupport.stream(ids.spliterator(), false).toList());
                })
                .idMatchConditionGenerator(Language.LANGUAGE.ID::eq)
                .validateForCreationFunction(language -> {
                    if (language.getName() == null || language.getName().isBlank()) {
                        return "Name is empty";
                    }

                    if (language.getId() != null) return "Language to be created has a non-null id";

                    return null;
                })
                .validateForUpdateFunction(language -> {
                    if (language.getName() == null || language.getName().isBlank()) {
                        return "Name is empty";
                    }

                    if (language.getId() == null) return "Language to be updated has a null id";


                    return null;
                })
                .build();
    }
}
