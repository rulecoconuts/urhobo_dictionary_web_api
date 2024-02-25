package com.fejiro.exploration.dictionary.dictionary_web_api.controller.rest.part_of_speech;

import com.fejiro.exploration.dictionary.dictionary_web_api.error_handling.IllegalArgumentExceptionWithMessageMap;
import com.fejiro.exploration.dictionary.dictionary_web_api.service.part_of_speech.PartOfSpeechDomainObject;
import com.fejiro.exploration.dictionary.dictionary_web_api.service.part_of_speech.PartOfSpeechService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/parts_of_speech")
public class PartOfSpeechRESTController {

    @Autowired
    PartOfSpeechService partOfSpeechService;

    @GetMapping("/nameSearch")
    ResponseEntity<Page<PartOfSpeechDomainObject>> searchByName(@RequestParam("name") String namePattern,
                                                                Pageable pageable) {
        return ResponseEntity.ok(partOfSpeechService.searchByName(namePattern, pageable));
    }

    @PostMapping
    ResponseEntity<PartOfSpeechDomainObject> create(
            @RequestBody PartOfSpeechDomainObject partOfSpeech) throws IllegalArgumentExceptionWithMessageMap {
        return ResponseEntity.ok(partOfSpeechService.create(partOfSpeech));
    }

    @PutMapping
    ResponseEntity<PartOfSpeechDomainObject> update(
            @RequestBody PartOfSpeechDomainObject partOfSpeechDomainObject) throws IllegalArgumentExceptionWithMessageMap {
        return ResponseEntity.ok(partOfSpeechService.update(partOfSpeechDomainObject));
    }

    @DeleteMapping("/{id}")
    ResponseEntity delete(@PathVariable Integer id) {
        partOfSpeechService.deleteById(id);

        return ResponseEntity.ok().build();
    }
}
