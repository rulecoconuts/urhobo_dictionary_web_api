package com.fejiro.exploration.dictionary.dictionary_web_api.data_transfer.word;

import com.fejiro.exploration.dictionary.dictionary_web_api.error_handling.IllegalArgumentExceptionWithMessageMap;
import com.fejiro.exploration.dictionary.dictionary_web_api.service.pronunciation.PronunciationDomainObject;
import com.fejiro.exploration.dictionary.dictionary_web_api.service.pronunciation.PronunciationService;
import com.fejiro.exploration.dictionary.dictionary_web_api.service.translation.TranslationContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class SimpleWordCreationRequestDTOValidator implements WordCreationRequestDTOValidator {
    @Autowired
    PronunciationService pronunciationService;

    /**
     * Throw an error if the creation request is invalid.
     *
     * @param creationRequestDTO
     * @throws IllegalArgumentExceptionWithMessageMap
     */
    @Override
    public void throwIfInvalid(
            WordCreationRequestDTO creationRequestDTO) throws IllegalArgumentExceptionWithMessageMap {
        Map<String, String> errors = validate(creationRequestDTO);

        if (!errors.isEmpty())
            throw new IllegalArgumentExceptionWithMessageMap("Invalid word creation request",
                                                             errors,
                                                             HttpStatus.BAD_REQUEST);
    }

    /**
     * Validate a word creation request
     *
     * @param creationRequestDTO
     * @return a map of errors
     */
    @Override
    public Map<String, String> validate(WordCreationRequestDTO creationRequestDTO) {
        Map<String, String> errors = new HashMap<>();

        // NAME
        if (creationRequestDTO.getName() == null) {
            errors.put("name", "Name is required");
        } else if (creationRequestDTO.getName().isBlank()) {
            errors.put("name", "Name cannot be blank");
        }

        // TRANSLATION CONTEXT
        putAllAddKeyPrefix(errors, validateTranslationContext(creationRequestDTO.getTranslationContext()),
                           "translation_context");

        // PARTS
        for (int i = 0; i < creationRequestDTO.getParts().size(); i++) {
            putAllAddKeyPrefix(errors, validatePart(creationRequestDTO.getParts().get(i)),
                               String.format("parts.%d", i));
        }

        return errors;
    }

    Map<String, String> validateTranslationContext(TranslationContext translationContext) {
        Map<String, String> errors = new HashMap<>();

        if (translationContext == null) {
            errors.put("_", "Cannot be null");
            return errors;
        }

        // SOURCE
        if (translationContext.getSource() == null) {
            errors.put("source", "Source cannot be null");
        } else if (translationContext.getSource().getId() == null) {
            errors.put("source.id", "ID cannot be null");
        } else if (translationContext.getSource().getId() < 1) {
            errors.put("source.id", "ID cannot be less than 1");
        }

        // TARGET
        if (translationContext.getSource() == null) {
            errors.put("target", "Target cannot be null");
        } else if (translationContext.getSource().getId() == null) {
            errors.put("target.id", "ID cannot be null");
        } else if (translationContext.getSource().getId() < 1) {
            errors.put("target.id", "ID cannot be less than 1");
        }

        // If no errors were found with source and target ids
        if (errors.isEmpty() && translationContext.getSource().getId().equals(translationContext.getTarget().getId())) {
            String duplicateError = "Source and target ID cannot be the same";
            errors.put("source.id", duplicateError);
            errors.put("target.id", duplicateError);
        }
        return errors;
    }

    /**
     * Put all the values of a source map into a destination map with prefixes added to the keys that will be put into
     * the destination.
     *
     * @param destination
     * @param source
     * @param prefix
     */
    static void putAllAddKeyPrefix(Map<String, String> destination, Map<String, String> source, String prefix) {
        for (String key : source.keySet()) {
            destination.put(String.format("%s.%s", prefix, key), source.get(key));
        }
    }

    /**
     * Validate part specification
     *
     * @param partSpecificationDTO
     * @return
     */
    Map<String, String> validatePart(WordCreationWordPartSpecificationDTO partSpecificationDTO) {
        Map<String, String> errors = new HashMap<>();

        if (partSpecificationDTO == null) {
            errors.put("_", "Cannot be null");
            return errors;
        }

        // PART ID
        if (partSpecificationDTO.getPart().getId() == null) {
            errors.put("part.id", "ID cannot be null");
        } else if (partSpecificationDTO.getPart().getId() < 1) {
            errors.put("part.id", "ID cannot be less than 1");
        }

        // DEFINITION

        // NOTE

        // PRONUNCIATIONS
        for (int i = 0; i < partSpecificationDTO.getPronunciations().size(); i++) {
            putAllAddKeyPrefix(errors, validatePronunciation(partSpecificationDTO.getPronunciations().get(i)),
                               String.format("pronunciations.%d", i));
        }

        // TRANSLATIONS

        for (int i = 0; i < partSpecificationDTO.getTranslations().size(); i++) {
            putAllAddKeyPrefix(errors, validateTranslation(partSpecificationDTO.getTranslations().get(i)),
                               String.format("translations.%d", i));
        }

        return errors;
    }

    /**
     * Validate pronunciation
     *
     * @param pronunciation
     * @return
     */
    Map<String, String> validatePronunciation(PronunciationDomainObject pronunciation) {
        Map<String, String> errors = pronunciationService.validateModelForCreation(pronunciation);

        errors.remove("word_part");

        return errors;
    }

    /**
     * Validate translation specification
     *
     * @param translationSpecificationDTO
     * @return
     */
    Map<String, String> validateTranslation(WordCreationTranslationSpecificationDTO translationSpecificationDTO) {
        Map<String, String> errors = new HashMap<>();

        if (translationSpecificationDTO == null) {
            errors.put("_", "Cannot be null");
            return errors;
        }

        // NOTE

        // WORD PART
        if (translationSpecificationDTO.getWordPart().getId() == null) {
            errors.put("word_part.id", "ID cannot be null");
        } else if (translationSpecificationDTO.getWordPart().getId() < 1) {
            errors.put("word_part.id", "ID cannot be less than 1");
        }

        return errors;
    }
}
