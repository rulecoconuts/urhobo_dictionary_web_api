package com.fejiro.exploration.dictionary.dictionary_web_api.controller.rest.translation;

import com.fejiro.exploration.dictionary.dictionary_web_api.error_handling.IllegalArgumentExceptionWithMessageMap;
import com.fejiro.exploration.dictionary.dictionary_web_api.service.translation.TranslationDomainObject;
import com.fejiro.exploration.dictionary.dictionary_web_api.service.translation.TranslationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/translations")
public class TranslationRESTController {

    @Autowired
    TranslationService translationService;

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
}
