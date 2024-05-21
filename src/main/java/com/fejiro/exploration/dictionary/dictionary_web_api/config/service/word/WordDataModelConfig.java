package com.fejiro.exploration.dictionary.dictionary_web_api.config.service.word;

import com.fejiro.exploration.dictionary.dictionary_web_api.database.CRUDDAO;
import com.fejiro.exploration.dictionary.dictionary_web_api.database.ConfigurableGenericJOOQCRUDDAO;
import com.fejiro.exploration.dictionary.dictionary_web_api.database.SimpleGeneralAuditablePopulator;
import com.fejiro.exploration.dictionary.dictionary_web_api.service.language.LanguageDataObject;
import com.fejiro.exploration.dictionary.dictionary_web_api.service.word.WordDataObject;
import com.fejiro.exploration.dictionary.dictionary_web_api.tables.Language;
import com.fejiro.exploration.dictionary.dictionary_web_api.tables.Word;
import com.fejiro.exploration.dictionary.dictionary_web_api.tables.records.LanguageRecord;
import com.fejiro.exploration.dictionary.dictionary_web_api.tables.records.WordRecord;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.util.stream.StreamSupport;

@Configuration
public class WordDataModelConfig {
    @Autowired
    DSLContext dsl;

    @Bean
    @Lazy
    CRUDDAO<WordDataObject, Long> wordCRUDDAO(SimpleGeneralAuditablePopulator auditablePopulator) {
        return ConfigurableGenericJOOQCRUDDAO
                .<WordDataObject, Long, WordRecord>builder()
                .modelClass(WordDataObject.class)
                .dslContext(dsl)
                .table(Word.WORD)
                .updatePreProcessFunction(language -> {
                    auditablePopulator.populateForUpdate(language);
                    return language;
                })
                .creationPreProcessFunction(language -> {
                    auditablePopulator.populateForCreation(language);
                    return language;
                })
                .idExtractionFunction(WordDataObject::getId)
                .idCollectionMatchConditionGenerator(ids -> {
                    return Word.WORD.ID.in(StreamSupport.stream(ids.spliterator(), false).toList());
                })
                .idMatchConditionGenerator(Word.WORD.ID::eq)
                .validateForCreationFunction(word -> {
                    if (word.getName() == null || word.getName().isBlank()) {
                        return "Name is empty";
                    }

                    if (word.getLanguageId() == null) {
                        return "Word Language id is required";
                    } else if (word.getLanguageId() < 1) {
                        return "Word Language id must be greater than 0";
                    }

                    if (word.getId() != null) return "Word to be created has a non-null id";

                    return null;
                })
                .validateForUpdateFunction(word -> {
                    if (word.getName() == null || word.getName().isBlank()) {
                        return "Name is empty";
                    }


                    if (word.getLanguageId() == null) {
                        return "Word Language id is required";
                    } else if (word.getLanguageId() < 1) {
                        return "Word Language id must be greater than 0";
                    }

                    if (word.getId() == null) return "Word to be updated has a null id";


                    return null;
                })
                .build();
    }
}
