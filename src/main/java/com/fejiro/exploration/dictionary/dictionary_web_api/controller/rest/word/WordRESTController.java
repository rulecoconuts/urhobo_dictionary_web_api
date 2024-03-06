package com.fejiro.exploration.dictionary.dictionary_web_api.controller.rest.word;

import com.fejiro.exploration.dictionary.dictionary_web_api.service.language.LanguageDomainObject;
import com.fejiro.exploration.dictionary.dictionary_web_api.service.word.WordDomainObject;
import com.fejiro.exploration.dictionary.dictionary_web_api.service.word.WordService;
import com.fejiro.exploration.dictionary.dictionary_web_api.service.word_part.FullWordPartDomainObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/words")
public class WordRESTController {

    @Autowired
    WordService wordService;

    @GetMapping("/nameSearch")
    ResponseEntity<Page<WordDomainObject>> searchByName(@RequestParam("name") String namePattern,
                                                        Pageable pageable) {
        return ResponseEntity.ok(wordService.searchByName(namePattern, pageable));
    }

    @GetMapping("/nameSearch/full")
    ResponseEntity<Page<FullWordPartDomainObject>> searchByNameFull(@RequestParam("name") String namePattern,
                                                                    Pageable pageable) {
        return ResponseEntity.ok(wordService.searchForFullWordPartByName(namePattern, pageable));
    }

    @GetMapping("language/{language_id}/nameSearch/full/")
    ResponseEntity<Page<FullWordPartDomainObject>> searchByNameFullInLanguage(
            @RequestParam("name") String namePattern,
            @PathVariable("language_id") Integer languageId,
            Pageable pageable) {
        return ResponseEntity.ok(wordService.searchByNameFullInLanguage(namePattern, LanguageDomainObject.builder()
                                                                                                         .id(languageId)
                                                                                                         .build(),
                                                                        pageable));
    }
}
