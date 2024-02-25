package com.fejiro.exploration.dictionary.dictionary_web_api.service.word;

import com.fejiro.exploration.dictionary.dictionary_web_api.database.CRUDDAO;
import com.fejiro.exploration.dictionary.dictionary_web_api.database.GenericJOOQCRUDDAO;
import com.fejiro.exploration.dictionary.dictionary_web_api.service.GenericJOOQBackedService;
import com.fejiro.exploration.dictionary.dictionary_web_api.service.part_of_speech.PartOfSpeechDomainObject;
import com.fejiro.exploration.dictionary.dictionary_web_api.service.word_part.FullWordPartDomainObject;
import com.fejiro.exploration.dictionary.dictionary_web_api.service.word_part.PartWordPartPairDomainObject;
import com.fejiro.exploration.dictionary.dictionary_web_api.service.word_part.WordPartDomainObject;
import com.fejiro.exploration.dictionary.dictionary_web_api.tables.Word;
import com.fejiro.exploration.dictionary.dictionary_web_api.tables.WordPart;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class CustomJOOQBackedWordService implements WordService, GenericJOOQBackedService<WordDomainObject, WordDataObject, Long> {
    @Autowired
    ConversionService conversionService;

    @Autowired
    CRUDDAO<WordDataObject, Long> wordCRUDDAO;

    @Override
    public ConversionService getConversionService() {
        return conversionService;
    }

    @Override
    public Class<? extends WordDomainObject> getDomainClass() {
        return WordDomainObject.class;
    }

    @Override
    public Class<? extends WordDataObject> getDataClass() {
        return WordDataObject.class;
    }

    @Override
    public CRUDDAO<WordDataObject, Long> getCRUDAO() {
        return wordCRUDDAO;
    }

    @Override
    public String generateErrorLabel(WordDomainObject model) {
        return String.format("%d_%s_%d", model.getId(), model.getName(), model.getLanguageId());
    }

    @Override
    public Long getId(WordDomainObject model) {
        return model.getId();
    }

    @Override
    public Map<String, String> validateModelForCreation(WordDomainObject model) {
        Map<String, String> errors = new HashMap<>();

        if (model.getId() != null) {
            errors.put("id", "ID must be null for a newly created word");
        }

        if (model.getName() == null) {
            errors.put("name", "Name is required");
        } else if (model.getName().isBlank()) {
            errors.put("name", "Name cannot be blank");
        }

        if (model.getLanguageId() == null) {
            errors.put("language", "Language is required");
        } else if (model.getLanguageId() < 1) {
            errors.put("language", "Language id must be greater than 0");
        }

        if (!errors.containsKey("name") && !errors.containsKey("language")) {
            // If name and language are valid, check if a matching word exists
            Optional<WordDomainObject> existingMatch
                    = retrieveOne(DSL.and(
                    Word.WORD.NAME.equalIgnoreCase(model.getName()),
                    Word.WORD.LANGUAGE_ID.eq(model.getLanguageId())
            ));

            if (existingMatch.isPresent()) {
                errors.put("duplicate", "Word is not unique");
            }
        }

        return errors;
    }

    @Override
    public Map<String, String> validateModelForUpdate(WordDomainObject model, Optional<WordDomainObject> existingCopy) {
        Map<String, String> errors = new HashMap<>();

        if (model.getId() == null) {
            errors.put("id", "ID must be non-null to update word");
        }

        if (model.getName() == null) {
            errors.put("name", "Name is required");
        } else if (model.getName().isBlank()) {
            errors.put("name", "Name cannot be blank");
        }

        if (model.getLanguageId() == null) {
            errors.put("language", "Language is required");
        } else if (model.getLanguageId() < 1) {
            errors.put("language", "Language id must be greater than 0");
        } else if (existingCopy.isPresent() && model.getLanguageId().equals(existingCopy.get().getLanguageId())) {
            errors.put("language", "Language cannot be changed");
        }

        if (!errors.containsKey("name") && !errors.containsKey("language") &&
                existingCopy.isPresent() && !existingCopy.get().getName().equalsIgnoreCase(
                model.getName()
        )) {
            // If name and language are valid, and name is changed, check if a word that matches the new name exists
            // in the language
            Optional<WordDomainObject> existingMatch
                    = retrieveOne(DSL.and(
                    Word.WORD.NAME.equalIgnoreCase(model.getName()),
                    Word.WORD.LANGUAGE_ID.eq(model.getLanguageId())
            ));

            if (existingMatch.isPresent()) {
                errors.put("duplicate", "Word is not unique");
            }
        }

        if (existingCopy.isEmpty()) {
            errors.put("id", "Word with id does not exist");
        }

        return errors;
    }

    @Override
    public Page<WordDomainObject> searchByName(String namePattern, Pageable pageable) {
        return getGenericJOOQDAO().retrieveAll(Word.WORD.NAME.like(namePattern), pageable)
                                  .map(this::toDomain);
    }

    @Override
    public Page<FullWordPartDomainObject> searchForFullWordPartByName(String namePattern, Pageable pageable) {
        var dsl = getGenericJOOQDAO()
                .getDsl();
        var results = dsl.select(Word.WORD,
                                 DSL.multiset(
                                            dsl.select(WordPart.WORD_PART, WordPart.WORD_PART.partOfSpeech())
                                               .from(WordPart.WORD_PART)
                                               .where(WordPart.WORD_PART.WORD_ID.eq(Word.WORD.ID)))
                                    .convertFrom(r -> r.map(
                                            innerRecord -> {
                                                return PartWordPartPairDomainObject.builder()
                                                                                   .wordPart(innerRecord.component1()
                                                                                                        .into(WordPartDomainObject.class))
                                                                                   .part(innerRecord.component2()
                                                                                                    .into(PartOfSpeechDomainObject.class))
                                                                                   .build();
                                            }
                                    ))

                         ).from(Word.WORD)
                         .where(Word.WORD.NAME.likeIgnoreCase(namePattern))
                         .orderBy(getGenericJOOQDAO().getSortFields(pageable.getSort()))
                         .limit(pageable.getPageSize())
                         .offset(pageable.getOffset())
                         .fetch(r -> {
                             return FullWordPartDomainObject.builder()
                                                            .word(r.component1().into(WordDomainObject.class))
                                                            .parts(r.component2())
                                                            .build();
                         });
        return new PageImpl<>(results, pageable,
                              getGenericJOOQDAO()
                                      .count(Word.WORD.NAME.likeIgnoreCase(namePattern)));
    }
}
