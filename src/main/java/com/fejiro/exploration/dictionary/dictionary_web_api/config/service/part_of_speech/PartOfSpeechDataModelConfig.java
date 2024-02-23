package com.fejiro.exploration.dictionary.dictionary_web_api.config.service.part_of_speech;

import com.fejiro.exploration.dictionary.dictionary_web_api.database.CRUDDAO;
import com.fejiro.exploration.dictionary.dictionary_web_api.database.ConfigurableGenericJOOQCRUDDAO;
import com.fejiro.exploration.dictionary.dictionary_web_api.database.SimpleGeneralAuditablePopulator;
import com.fejiro.exploration.dictionary.dictionary_web_api.service.part_of_speech.PartOfSpeechDataObject;
import com.fejiro.exploration.dictionary.dictionary_web_api.service.word.WordDataObject;
import com.fejiro.exploration.dictionary.dictionary_web_api.tables.PartOfSpeech;
import com.fejiro.exploration.dictionary.dictionary_web_api.tables.Word;
import com.fejiro.exploration.dictionary.dictionary_web_api.tables.records.PartOfSpeechRecord;
import com.fejiro.exploration.dictionary.dictionary_web_api.tables.records.WordRecord;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.util.stream.StreamSupport;

@Configuration
public class PartOfSpeechDataModelConfig {
    @Autowired
    DSLContext dsl;

    @Bean
    @Lazy
    CRUDDAO<PartOfSpeechDataObject, Integer> partOfSpeechDataObjectIntegerCRUDDAO(
            SimpleGeneralAuditablePopulator auditablePopulator) {
        return ConfigurableGenericJOOQCRUDDAO
                .<PartOfSpeechDataObject, Integer, PartOfSpeechRecord>builder()
                .modelClass(PartOfSpeechDataObject.class)
                .dslContext(dsl)
                .table(PartOfSpeech.PART_OF_SPEECH)
                .updatePreProcessFunction(part -> {
                    auditablePopulator.populateForUpdate(part);
                    return part;
                })
                .creationPreProcessFunction(part -> {
                    auditablePopulator.populateForCreation(part);
                    return part;
                })
                .idExtractionFunction(PartOfSpeechDataObject::getId)
                .idCollectionMatchConditionGenerator(ids -> {
                    return PartOfSpeech.PART_OF_SPEECH.ID.in(StreamSupport.stream(ids.spliterator(), false).toList());
                })
                .idMatchConditionGenerator(PartOfSpeech.PART_OF_SPEECH.ID::eq)
                .validateForCreationFunction(part -> {
                    if (part.getName() == null || part.getName().isBlank()) {
                        return "Name is empty";
                    }


                    if (part.getId() != null) return "Part of speech to be created has a non-null id";

                    return null;
                })
                .validateForUpdateFunction(part -> {
                    if (part.getName() == null || part.getName().isBlank()) {
                        return "Name is empty";
                    }


                    if (part.getId() == null) return "Part of speech to be updated has a null id";


                    return null;
                })
                .build();
    }
}
