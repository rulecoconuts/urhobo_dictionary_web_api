package com.fejiro.exploration.dictionary.dictionary_web_api.config.service.pronunciation;

import com.fejiro.exploration.dictionary.dictionary_web_api.database.CRUDDAO;
import com.fejiro.exploration.dictionary.dictionary_web_api.database.ConfigurableGenericJOOQCRUDDAO;
import com.fejiro.exploration.dictionary.dictionary_web_api.database.SimpleGeneralAuditablePopulator;
import com.fejiro.exploration.dictionary.dictionary_web_api.service.pronunciation.PronunciationDataObject;
import com.fejiro.exploration.dictionary.dictionary_web_api.service.translation.TranslationDataObject;
import com.fejiro.exploration.dictionary.dictionary_web_api.tables.Pronunciation;
import com.fejiro.exploration.dictionary.dictionary_web_api.tables.Translation;
import com.fejiro.exploration.dictionary.dictionary_web_api.tables.records.PronunciationRecord;
import com.fejiro.exploration.dictionary.dictionary_web_api.tables.records.TranslationRecord;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.util.stream.StreamSupport;

@Configuration
public class PronunciationDataModelConfig {

    @Autowired
    DSLContext dsl;

    @Bean
    @Lazy
    CRUDDAO<PronunciationDataObject, Long> pronunciationDataObjectLongCRUDDAO(
            SimpleGeneralAuditablePopulator auditablePopulator) {
        return ConfigurableGenericJOOQCRUDDAO
                .<PronunciationDataObject, Long, PronunciationRecord>builder()
                .modelClass(PronunciationDataObject.class)
                .dslContext(dsl)
                .table(Pronunciation.PRONUNCIATION)
                .updatePreProcessFunction(translation -> {
                    auditablePopulator.populateForUpdate(translation);
                    return translation;
                })
                .creationPreProcessFunction(translation -> {
                    auditablePopulator.populateForCreation(translation);
                    return translation;
                })
                .idExtractionFunction(PronunciationDataObject::getId)
                .idCollectionMatchConditionGenerator(ids -> {
                    return Pronunciation.PRONUNCIATION.ID.in(StreamSupport.stream(ids.spliterator(), false).toList());
                })
                .idMatchConditionGenerator(Pronunciation.PRONUNCIATION.ID::eq)
                .validateForCreationFunction(pronunciation -> {
                    if (pronunciation.getWordPartId() == null) {
                        return "Word part id is required";
                    } else if (pronunciation.getWordPartId() < 1) {
                        return "Word part id must be greater than 0";
                    }

                    if (pronunciation.getAudioUrl() == null || pronunciation.getAudioUrl().isBlank()) {
                        return "Audio url is required";
                    }

                    if (pronunciation.getId() != null) return "Pronunciation to be created has a non-null id";

                    return null;
                })
                .validateForUpdateFunction(pronunciation -> {
                    if (pronunciation.getWordPartId() == null) {
                        return "Word part id is required";
                    } else if (pronunciation.getWordPartId() < 1) {
                        return "Word part id must be greater than 0";
                    }

                    if (pronunciation.getAudioUrl() == null || pronunciation.getAudioUrl().isBlank()) {
                        return "Audio url is required";
                    }

                    if (pronunciation.getId() == null) return "Pronunciation to be updated has a null id";


                    return null;
                })
                .build();
    }
}
