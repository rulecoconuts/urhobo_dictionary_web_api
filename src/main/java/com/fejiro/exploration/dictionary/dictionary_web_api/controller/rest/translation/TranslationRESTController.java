package com.fejiro.exploration.dictionary.dictionary_web_api.controller.rest.translation;

import com.fejiro.exploration.dictionary.dictionary_web_api.error_handling.IllegalArgumentExceptionWithMessageMap;
import com.fejiro.exploration.dictionary.dictionary_web_api.service.language.CustomJOOQBackedLanguageService;
import com.fejiro.exploration.dictionary.dictionary_web_api.service.language.LanguageService;
import com.fejiro.exploration.dictionary.dictionary_web_api.service.translation.TranslationContext;
import com.fejiro.exploration.dictionary.dictionary_web_api.service.translation.TranslationDomainObject;
import com.fejiro.exploration.dictionary.dictionary_web_api.service.translation.TranslationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/api/translations")
public class TranslationRESTController {

    @Autowired
    TranslationService translationService;

    @Autowired
    LanguageService languageService;

    @PutMapping
    ResponseEntity updateTranslations(
            @RequestBody TranslationDomainObject newTranslation) throws IllegalArgumentExceptionWithMessageMap {
        return ResponseEntity.ok(translationService.update(newTranslation));
    }

    @PostMapping
    ResponseEntity create(
            @RequestBody TranslationDomainObject newTranslation) throws IllegalArgumentExceptionWithMessageMap {
        return ResponseEntity.ok(translationService.create(newTranslation));
    }


    /**
     * Validate the translation context.
     * A context is valid if both the source, and target exist
     *
     * @param translationContext
     * @return
     */
    @PostMapping("/context/validate")
    ResponseEntity validateTranslationContext(@RequestBody TranslationContext translationContext) {
        var foundI = ((CustomJOOQBackedLanguageService) languageService)
                .retrieveAllById(
                        List.of(translationContext.getSource().getId(), translationContext.getTarget().getId()));

        var foundList = StreamSupport.stream(foundI.spliterator(), false).toList();

        if (foundList.size() < 2) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        return ResponseEntity.ok().build();
    }
}
