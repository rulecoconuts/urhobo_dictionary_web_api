package com.fejiro.exploration.dictionary.dictionary_web_api.controller.rest.word_part;

import com.fejiro.exploration.dictionary.dictionary_web_api.error_handling.IllegalArgumentExceptionWithMessageMap;
import com.fejiro.exploration.dictionary.dictionary_web_api.service.language.LanguageDomainObject;
import com.fejiro.exploration.dictionary.dictionary_web_api.service.pronunciation.PronunciationDomainObject;
import com.fejiro.exploration.dictionary.dictionary_web_api.service.pronunciation.PronunciationService;
import com.fejiro.exploration.dictionary.dictionary_web_api.service.translation.FullTranslation;
import com.fejiro.exploration.dictionary.dictionary_web_api.service.translation.TranslationService;
import com.fejiro.exploration.dictionary.dictionary_web_api.service.word_part.WordPartDomainObject;
import com.fejiro.exploration.dictionary.dictionary_web_api.service.word_part.WordPartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/api/word_parts")
public class WordPartRESTController {

    @Autowired
    WordPartService wordPartService;

    @Autowired
    PronunciationService pronunciationService;

    @Autowired
    TranslationService translationService;

    /**
     * Update a single word part domain model
     *
     * @param wordPart
     * @return
     */
    @PutMapping
    ResponseEntity<WordPartDomainObject> update(
            @RequestBody WordPartDomainObject wordPart) throws IllegalArgumentExceptionWithMessageMap {
        return ResponseEntity.ok(wordPartService.update(wordPart));
    }

    @PostMapping
    ResponseEntity<WordPartDomainObject> create(
            @RequestBody WordPartDomainObject wordPart) throws IllegalArgumentExceptionWithMessageMap {
        return ResponseEntity.ok(wordPartService.create(wordPart));
    }

    @DeleteMapping("/{id}")
    ResponseEntity delete(@PathVariable Long id) {
        wordPartService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{word_part_id}/pronunciations")
    ResponseEntity<List<PronunciationDomainObject>> getPronunciations(@PathVariable("word_part_id") Long wordPartId) {
        var iterable = pronunciationService.getPronunciationsOfWordPart(WordPartDomainObject.builder()
                                                                                            .id(wordPartId)
                                                                                            .build());

        return ResponseEntity.ok(StreamSupport.stream(iterable.spliterator(), false)
                                              .toList());
    }

    /**
     * Fetch full translations list
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}/translations/full/languages/{target_language_id}")
    ResponseEntity<List<FullTranslation>> getFullTranslations(@PathVariable("id") Long id,
                                                              @PathVariable("target_language_id") Integer targetLanguageId) {
        WordPartDomainObject wordPart = WordPartDomainObject.builder()
                                                            .id(id)
                                                            .build();

        LanguageDomainObject targetLanguage = LanguageDomainObject.builder()
                                                                  .id(targetLanguageId)
                                                                  .build();
        return ResponseEntity.ok(
                StreamSupport.stream(translationService.fetchTranslations(wordPart, targetLanguage).spliterator(),
                                     false).toList());
    }
}
