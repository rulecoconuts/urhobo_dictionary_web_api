package com.fejiro.exploration.dictionary.dictionary_web_api.service.translation;

import com.fejiro.exploration.dictionary.dictionary_web_api.database.CRUDDAO;
import com.fejiro.exploration.dictionary.dictionary_web_api.database.GenericJOOQCRUDDAO;
import com.fejiro.exploration.dictionary.dictionary_web_api.service.GenericJOOQBackedService;
import com.fejiro.exploration.dictionary.dictionary_web_api.service.language.LanguageDomainObject;
import com.fejiro.exploration.dictionary.dictionary_web_api.service.word.WordDataObject;
import com.fejiro.exploration.dictionary.dictionary_web_api.service.word.WordDomainObject;
import com.fejiro.exploration.dictionary.dictionary_web_api.service.word_part.WordPartDomainObject;
import com.fejiro.exploration.dictionary.dictionary_web_api.tables.Translation;
import io.micrometer.observation.annotation.Observed;
import org.jooq.Condition;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.StreamSupport;

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
                    getSourceAndTargetPairEqualCondition(model)
            );
            if (matchingTranslation.isPresent()) {
                errors.put("duplicate", String.format("Translation of %d to %d", model.getSourceWordPartId(),
                                                      model.getTargetWordPartId()));
            }
        }

        return errors;
    }

    Condition getSourceAndTargetPairEqualCondition(TranslationDomainObject model) {
        // Source and target pairs are unordered.
        // So {source=word1, target=word2} is equal to {target=word1, source=word2}
        var normal = DSL.and(Translation.TRANSLATION
                                     .SOURCE_WORD_PART_ID.eq(
                                             model.getSourceWordPartId()),
                             Translation.TRANSLATION.TARGET_WORD_PART_ID.eq(
                                     model.getTargetWordPartId()));
        var flipped = DSL.and(Translation.TRANSLATION
                                      .SOURCE_WORD_PART_ID.eq(
                                              model.getTargetWordPartId()),
                              Translation.TRANSLATION.TARGET_WORD_PART_ID.eq(
                                      model.getSourceWordPartId()));
        return DSL.or(
                normal, flipped
        );
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

        if (!errors.containsKey("source_word_part") && !errors.containsKey(
                "target_word_part") && model.getSourceWordPartId().equals(model.getTargetWordPartId())) {
            String sharedMessage = "Source and target word part cannot be the same";
            errors.put("source_word_part", sharedMessage);
            errors.put("target_word_part", sharedMessage);
        }
    }

    /**
     * Validate multiple translations before they are created
     *
     * @param models
     * @return
     */
    @Override
    public Map<TranslationDomainObject, Map<String, String>> validateModelsForCreation(
            Iterable<TranslationDomainObject> models) {
        Map<TranslationDomainObject, Map<String, String>> errors = new HashMap<>();
        List<TranslationDomainObject> modelList = StreamSupport.stream(models.spliterator(), false)
                                                               .toList();

        // Perform simple shared validation that does not require expensive queries
        modelList.forEach(model -> performSharedModelValidationForCreation(model, errors));

        // Check for duplicates of any of the translation source and target pairs
        var condition = DSL.or(modelList
                                       .stream().map(this::getSourceAndTargetPairEqualCondition).toList());

        List<TranslationDomainObject> matchingTranslations = StreamSupport.stream(retrieveAll(condition).spliterator(),
                                                                                  false)
                                                                          .toList();

        modelList.forEach(model -> performMatchingTranslationValidationForCreation(model,
                                                                                   matchingTranslations,
                                                                                   errors));


        return GenericJOOQBackedService.super.validateModelsForCreation(models);
    }

    /**
     * Validate that model is a unique translation
     *
     * @param model
     * @param matchingTranslations
     * @param errors
     */
    void performMatchingTranslationValidationForCreation(TranslationDomainObject model,
                                                         List<TranslationDomainObject> matchingTranslations,
                                                         Map<TranslationDomainObject, Map<String, String>> errors) {
        // Only try to find match if the model does not contain any previous errors
        if (errors.containsKey(model)) return;
        var matchOptional = matchingTranslations.stream().filter(model::equalUnordered)
                                                .findFirst();

        if (matchOptional.isEmpty()) return;

        Map<String, String> translationSpecificErrors = new HashMap<>();

        translationSpecificErrors.put("_", "Source and target word-part pair not unique");
    }

    void performSharedModelValidationForCreation(TranslationDomainObject model,
                                                 Map<TranslationDomainObject, Map<String, String>> errors) {
        Map<String, String> modelSpecificErrors = errors.get(model);
        boolean didModelStartWithErrors = modelSpecificErrors != null;

        if (!didModelStartWithErrors) {
            modelSpecificErrors = new HashMap<>();
        }

        if (model.getId() != null) {
            modelSpecificErrors.put("id", "ID must be null for new translation");
        }

        performSharedValidation(model, modelSpecificErrors);

        if (!didModelStartWithErrors && !modelSpecificErrors.isEmpty()) {
            errors.put(model, modelSpecificErrors);
        }
    }

    /**
     * Fetch translations that contain the word part as a source or target
     *
     * @param wordPart
     * @return
     */
    @Override
    @Observed(name = "fetchTranslations")
    public Iterable<FullTranslation> fetchTranslations(WordPartDomainObject wordPart,
                                                       LanguageDomainObject targetLanguage) {

        var dsl = getGenericJOOQDAO().getDsl();
        var isSource = DSL.and(Translation.TRANSLATION.SOURCE_WORD_PART_ID.eq(wordPart.getId()),
                               Translation.TRANSLATION.fkTranslationTargetWord().word().LANGUAGE_ID.eq(
                                       targetLanguage.getId())
        );
        var isTarget = DSL.and(Translation.TRANSLATION.TARGET_WORD_PART_ID.eq(wordPart.getId()),
                               Translation.TRANSLATION.fkTranslationSourceWord().word().LANGUAGE_ID.eq(
                                       targetLanguage.getId())
        );
        var condition = DSL.or(isSource, isTarget);
        var results = dsl.select(Translation.TRANSLATION,
                                 Translation.TRANSLATION.fkTranslationSourceWord(),
                                 Translation.TRANSLATION.fkTranslationSourceWord().word(),
                                 Translation.TRANSLATION.fkTranslationTargetWord(),
                                 Translation.TRANSLATION.fkTranslationTargetWord().word())
                         .from(Translation.TRANSLATION)
                         .where(condition)
                         .orderBy(Translation.TRANSLATION.ID)
                         .fetch(record -> FullTranslation.builder()
                                                         .translation(record.component1()
                                                                            .into(TranslationDomainObject.class))
                                                         .sourceWordPart(
                                                                 record.component2().into(WordPartDomainObject.class))
                                                         .sourceWord(record.component3().into(WordDomainObject.class))
                                                         .targetWordPart(
                                                                 record.component4().into(WordPartDomainObject.class))
                                                         .targetWord(record.component5().into(WordDomainObject.class))
                                                         .build());
        return results;
    }
}
