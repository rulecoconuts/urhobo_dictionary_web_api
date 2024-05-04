package com.fejiro.exploration.dictionary.dictionary_web_api.service.pronunciation;

import com.fejiro.exploration.dictionary.dictionary_web_api.config.service.s3.LangresusS3Config;
import com.fejiro.exploration.dictionary.dictionary_web_api.database.CRUDDAO;
import com.fejiro.exploration.dictionary.dictionary_web_api.service.GenericJOOQBackedService;
import com.fejiro.exploration.dictionary.dictionary_web_api.service.s3_utils.S3Utils;
import com.fejiro.exploration.dictionary.dictionary_web_api.service.word_part.WordPartDomainObject;
import com.fejiro.exploration.dictionary.dictionary_web_api.tables.Pronunciation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class CustomJOOQBackedPronunciationService implements PronunciationService, GenericJOOQBackedService<PronunciationDomainObject, PronunciationDataObject, Long> {

    @Autowired
    ConversionService conversionService;

    @Autowired
    CRUDDAO<PronunciationDataObject, Long> pronunciationDataObjectLongCRUDDAO;

    @Autowired
    S3Utils s3Utils;

    @Override
    public ConversionService getConversionService() {
        return conversionService;
    }

    @Override
    public Class<? extends PronunciationDomainObject> getDomainClass() {
        return PronunciationDomainObject.class;
    }

    @Override
    public Class<? extends PronunciationDataObject> getDataClass() {
        return PronunciationDataObject.class;
    }

    @Override
    public CRUDDAO<PronunciationDataObject, Long> getCRUDAO() {
        return pronunciationDataObjectLongCRUDDAO;
    }

    @Override
    public String generateErrorLabel(PronunciationDomainObject model) {
        return String.format("%d_%d", model.getId(), model.getWordPartId());
    }

    @Override
    public Long getId(PronunciationDomainObject model) {
        return model.getId();
    }

    @Override
    public Map<String, String> validateModelForCreation(PronunciationDomainObject model) {
        Map<String, String> errors = new HashMap<>();

        if (model.getId() != null) {
            errors.put("id", "ID must be null when creating pronunciation");
        }

        if (model.getWordPartId() == null) {
            errors.put("word_part", "Word part is required");
        } else if (model.getWordPartId() < 1) {

            errors.put("word_part", "Word part id must be greater than 0");
        }

        if (model.getAudioUrl() == null) {
            errors.put("audio_url", "Audio URL is required");
        } else if (model.getAudioUrl().isBlank()) {
            errors.put("audio_url", "Audio URL cannot be blank");
        }

        return errors;
    }

    @Override
    public Map<String, String> validateModelForUpdate(PronunciationDomainObject model,
                                                      Optional<PronunciationDomainObject> existingCopy) {
        Map<String, String> errors = new HashMap<>();

        if (model.getId() == null) {
            errors.put("id", "ID must be non-null to update pronunciation");
        }

        if (model.getWordPartId() == null) {
            errors.put("word_part", "Word part is required");
        } else if (model.getWordPartId() < 1) {

            errors.put("word_part", "Word part id must be greater than 0");
        } else if (existingCopy.isPresent() && !existingCopy.get().getWordPartId().equals(model.getWordPartId())) {
            errors.put("word_part", "Word part of pronunciation should not be changed");
        }


        if (model.getAudioUrl() == null) {
            errors.put("audio_url", "Audio URL is required");
        } else if (model.getAudioUrl().isBlank()) {
            errors.put("audio_url", "Audio URL cannot be blank");
        }

        if (existingCopy.isEmpty()) {
            errors.put("id", "Pronunciation with ID does not exist");
        }


        return errors;
    }

    /**
     * Validate multiple pronunciations before creations.
     * Note that this just calls {@link com.fejiro.exploration.dictionary.dictionary_web_api.service.CreationService#validateModelForCreation(Object) ValidateModelForCreation}
     * for each pronunciation. This is allowed because the ValidateModelForCreation method does not perform any expensive DB operations.
     *
     * @param models
     * @return
     */
    @Override
    public Map<PronunciationDomainObject, Map<String, String>> validateModelsForCreation(
            Iterable<PronunciationDomainObject> models) {
        Map<PronunciationDomainObject, Map<String, String>> errors = new HashMap<>();
        for (var model : models) {
            Map<String, String> modelSpecificErrors = validateModelForCreation(model);
            if (!modelSpecificErrors.isEmpty()) {
                errors.put(model, modelSpecificErrors);
            }
        }

        return errors;
    }

    @Override
    public Iterable<PronunciationDomainObject> getPronunciationsOfWordPart(WordPartDomainObject wordPart) {
        return retrieveAll(Pronunciation.PRONUNCIATION.WORD_PART_ID.eq(wordPart.getId()));
    }

    @Override
    public void deleteById(Long id) {

        Optional<PronunciationDomainObject> pronunciation = retrieveById(id);
        if (pronunciation.isEmpty()) return;
        // Delete in DB
        delete(pronunciation.get());

        // Delete pronunciation audio file
        s3Utils.deleteObject(pronunciation.get().getAudioUrl());
    }
}
