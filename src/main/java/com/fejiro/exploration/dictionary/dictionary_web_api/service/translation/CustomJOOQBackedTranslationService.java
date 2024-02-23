package com.fejiro.exploration.dictionary.dictionary_web_api.service.translation;

import com.fejiro.exploration.dictionary.dictionary_web_api.database.CRUDDAO;
import com.fejiro.exploration.dictionary.dictionary_web_api.database.GenericJOOQCRUDDAO;
import com.fejiro.exploration.dictionary.dictionary_web_api.service.GenericJOOQBackedService;
import com.fejiro.exploration.dictionary.dictionary_web_api.service.word.WordDataObject;
import com.fejiro.exploration.dictionary.dictionary_web_api.tables.Translation;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class CustomJOOQBackedTranslationService implements TranslationService, GenericJOOQBackedService<TranslationDomainObject, TranslationDataObject, Long> {
    @Autowired
    ConversionService conversionService;

    @Autowired
    CRUDDAO<TranslationDataObject, Long> translationDataObjectLongCRUDDAO;

    @Override
    public ConversionService getConversionService() {
        return conversionService;
    }

    @Override
    public Class<? extends TranslationDomainObject> getDomainClass() {
        return TranslationDomainObject.class;
    }

    @Override
    public Class<? extends TranslationDataObject> getDataClass() {
        return TranslationDataObject.class;
    }

    @Override
    public CRUDDAO<TranslationDataObject, Long> getCRUDAO() {
        return translationDataObjectLongCRUDDAO;
    }

    @Override
    public String generateErrorLabel(TranslationDomainObject model) {
        return String.format("%d_%d_%d", model.getId(), model.getSourceWordPartId(), model.getTargetWordPartId());
    }

    @Override
    public Long getId(TranslationDomainObject model) {
        return model.getId();
    }

    @Override
    public Map<String, String> validateModelForCreation(TranslationDomainObject model) {
        Map<String, String> errors = new HashMap<>();

        if (model.getId() != null) {
            errors.put("id", "ID must be null for new translation");
        }

        performSharedValidation(model, errors);

        if (!errors.containsKey("source_word_part") && !errors.containsKey("target_word_part")) {
            // If source and target are valid, check if a translation for this pair already exists
            Optional<TranslationDomainObject> matchingTranslation = retrieveOne(
                    DSL.and(Translation.TRANSLATION
                                    .SOURCE_WORD_PART_ID.eq(
                                            model.getSourceWordPartId()),
                            Translation.TRANSLATION.TARGET_WORD_PART_ID.eq(
                                    model.getTargetWordPartId())
                    )
            );
            if (matchingTranslation.isPresent()) {
                errors.put("duplicate", String.format("Translation of %d to %d", model.getSourceWordPartId(),
                                                      model.getTargetWordPartId()));
            }
        }

        return errors;
    }

    @Override
    public Map<String, String> validateModelForUpdate(TranslationDomainObject model,
                                                      Optional<TranslationDomainObject> existingCopy) {
        Map<String, String> errors = new HashMap<>();

        if (model.getId() == null) {
            errors.put("id", "ID must be non-null for new translation");
        }

        performSharedValidation(model, errors);

        if (existingCopy.isEmpty()) {
            errors.put("id", "Translation ID does not exist in the database");
        } else {
            var translationCopy = existingCopy.get();

            // It should not be possible to change source or target for translation
            if (!translationCopy.getSourceWordPartId().equals(model.getSourceWordPartId())) {
                errors.put("source_word_part", "Translation source cannot be changed");
            }

            if (!translationCopy.getTargetWordPartId().equals(model.getTargetWordPartId())) {
                errors.put("target_word_part", "Translation target cannot be changed");
            }
        }

        return errors;
    }

    /**
     * Perform validation logic that is shared across creation and update of translation
     *
     * @param model
     * @param errors
     */
    private void performSharedValidation(TranslationDomainObject model, Map<String, String> errors) {
        if (model.getSourceWordPartId() == null) {
            errors.put("source_word_part", "Source word part is required");
        } else if (model.getSourceWordPartId() < 1) {
            errors.put("source_word_part", "Source word part must be greater than 0");
        }

        if (model.getTargetWordPartId() == null) {
            errors.put("target_word_part", "Target word part is required");
        } else if (model.getTargetWordPartId() < 1) {
            errors.put("target_word_part", "Target word part must be greater than 0");
        }
    }
}
