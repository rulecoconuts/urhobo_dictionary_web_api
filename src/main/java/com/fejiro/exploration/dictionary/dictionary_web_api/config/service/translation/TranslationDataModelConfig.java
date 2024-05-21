package com.fejiro.exploration.dictionary.dictionary_web_api.config.service.translation;

import com.fejiro.exploration.dictionary.dictionary_web_api.database.CRUDDAO;
import com.fejiro.exploration.dictionary.dictionary_web_api.database.ConfigurableGenericJOOQCRUDDAO;
import com.fejiro.exploration.dictionary.dictionary_web_api.database.SimpleGeneralAuditablePopulator;
import com.fejiro.exploration.dictionary.dictionary_web_api.service.translation.TranslationDataObject;
import com.fejiro.exploration.dictionary.dictionary_web_api.service.word.WordDataObject;
import com.fejiro.exploration.dictionary.dictionary_web_api.tables.Translation;
import com.fejiro.exploration.dictionary.dictionary_web_api.tables.Word;
import com.fejiro.exploration.dictionary.dictionary_web_api.tables.records.TranslationRecord;
import com.fejiro.exploration.dictionary.dictionary_web_api.tables.records.WordRecord;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.util.stream.StreamSupport;

@Configuration
public class TranslationDataModelConfig {
    @Autowired
    DSLContext dsl;

    @Bean
    @Lazy
    CRUDDAO<TranslationDataObject, Long> translationDataObjectLongCRUDDAO(
            SimpleGeneralAuditablePopulator auditablePopulator) {
        return ConfigurableGenericJOOQCRUDDAO
                .<TranslationDataObject, Long, TranslationRecord>builder()
                .modelClass(TranslationDataObject.class)
                .dslContext(dsl)
                .table(Translation.TRANSLATION)
                .updatePreProcessFunction(translation -> {
                    auditablePopulator.populateForUpdate(translation);
                    return translation;
                })
                .creationPreProcessFunction(translation -> {
                    auditablePopulator.populateForCreation(translation);
                    return translation;
                })
                .idExtractionFunction(TranslationDataObject::getId)
                .idCollectionMatchConditionGenerator(ids -> {
                    return Translation.TRANSLATION.ID.in(StreamSupport.stream(ids.spliterator(), false).toList());
                })
                .idMatchConditionGenerator(Translation.TRANSLATION.ID::eq)
                .validateForCreationFunction(translation -> {
                    if (translation.getSourceWordPartId() == null) {
                        return "Source word part id is required";
                    } else if (translation.getSourceWordPartId() < 1) {
                        return "Source word part id must be greater than 0";
                    }

                    if (translation.getTargetWordPartId() == null) {
                        return "Target word part id is required";
                    } else if (translation.getTargetWordPartId() < 1) {
                        return "Target word part id must be greater than 0";
                    }

                    if (translation.getId() != null) return "Translation to be created has a non-null id";

                    return null;
                })
                .validateForUpdateFunction(translation -> {
                    if (translation.getSourceWordPartId() == null) {
                        return "Source word part id is required";
                    } else if (translation.getSourceWordPartId() < 1) {
                        return "Source word part id must be greater than 0";
                    }

                    if (translation.getTargetWordPartId() == null) {
                        return "Target word part id is required";
                    } else if (translation.getTargetWordPartId() < 1) {
                        return "Target word part id must be greater than 0";
                    }

                    if (translation.getId() == null) return "Translation to be updated has a null id";


                    return null;
                })
                .build();
    }
}
