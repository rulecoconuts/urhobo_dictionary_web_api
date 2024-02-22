package com.fejiro.exploration.dictionary.dictionary_web_api.config.service.word_part;

import com.fejiro.exploration.dictionary.dictionary_web_api.database.CRUDDAO;
import com.fejiro.exploration.dictionary.dictionary_web_api.database.ConfigurableGenericJOOQCRUDDAO;
import com.fejiro.exploration.dictionary.dictionary_web_api.database.SimpleGeneralAuditablePopulator;
import com.fejiro.exploration.dictionary.dictionary_web_api.service.part_of_speech.PartOfSpeechDataObject;
import com.fejiro.exploration.dictionary.dictionary_web_api.service.word_part.WordPartDataObject;
import com.fejiro.exploration.dictionary.dictionary_web_api.tables.PartOfSpeech;
import com.fejiro.exploration.dictionary.dictionary_web_api.tables.WordPart;
import com.fejiro.exploration.dictionary.dictionary_web_api.tables.records.PartOfSpeechRecord;
import com.fejiro.exploration.dictionary.dictionary_web_api.tables.records.WordPartRecord;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.util.stream.StreamSupport;

@Configuration
public class WordPartDataModelConfig {

    @Autowired
    DSLContext dsl;

    @Bean
    @Lazy
    CRUDDAO<WordPartDataObject, Long> wordPartDataObjectLongCRUDDAO(
            SimpleGeneralAuditablePopulator auditablePopulator) {
        return ConfigurableGenericJOOQCRUDDAO
                .<WordPartDataObject, Long, WordPartRecord>builder()
                .modelClass(WordPartDataObject.class)
                .dslContext(dsl)
                .table(WordPart.WORD_PART)
                .updatePreProcessFunction(part -> {
                    auditablePopulator.populateForUpdate(part);
                    return part;
                })
                .creationPreProcessFunction(part -> {
                    auditablePopulator.populateForCreation(part);
                    return part;
                })
                .idExtractionFunction(WordPartDataObject::getId)
                .idCollectionMatchConditionGenerator(ids -> {
                    return WordPart.WORD_PART.ID.in(StreamSupport.stream(ids.spliterator(), false).toList());
                })
                .idMatchConditionGenerator(WordPart.WORD_PART.ID::eq)
                .validateForCreationFunction(word -> {
                    if (word.getId() != null) return "WordPart to be created has a non-null id";

                    return null;
                })
                .validateForUpdateFunction(word -> {

                    if (word.getId() == null) return "WordPart to be updated has a null id";


                    return null;
                })
                .build();
    }
}
