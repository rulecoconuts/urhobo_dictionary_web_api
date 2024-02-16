package com.fejiro.exploration.dictionary.dictionary_web_api.controller.rest.language;

import com.fejiro.exploration.dictionary.dictionary_web_api.error_handling.IllegalArgumentExceptionWithMessageMap;
import com.fejiro.exploration.dictionary.dictionary_web_api.service.language.LanguageDomainObject;
import com.fejiro.exploration.dictionary.dictionary_web_api.service.language.LanguageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/languages")
public class LanguageController {

    @Autowired
    LanguageService languageService;

    /**
     * Search for languages with names that match a pattern
     *
     * @param namePattern
     * @param pageable
     * @return
     */
    @GetMapping("/nameSearch")
    ResponseEntity<Page<LanguageDomainObject>> searchByName(@RequestParam("name") String namePattern,
                                                            Pageable pageable) {
        return ResponseEntity.ok(languageService.searchByName(namePattern, pageable));
    }

    /**
     * Create a new language
     *
     * @param language
     * @return
     * @throws IllegalArgumentExceptionWithMessageMap
     */
    @PostMapping
    ResponseEntity<LanguageDomainObject> create(
            @RequestBody LanguageDomainObject language) throws IllegalArgumentExceptionWithMessageMap {
        return ResponseEntity.ok(languageService.create(language));
    }

    /**
     * Delete a language
     *
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    ResponseEntity delete(@PathVariable Integer id) {
        languageService.deleteById(id);

        return ResponseEntity.ok().build();
    }
}
