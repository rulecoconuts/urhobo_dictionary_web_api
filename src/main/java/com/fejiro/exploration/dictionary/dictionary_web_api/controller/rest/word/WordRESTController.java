package com.fejiro.exploration.dictionary.dictionary_web_api.controller.rest.word;

import com.fejiro.exploration.dictionary.dictionary_web_api.data_transfer.word.*;
import com.fejiro.exploration.dictionary.dictionary_web_api.error_handling.ApiExceptionWithComplexObjectMessageMap;
import com.fejiro.exploration.dictionary.dictionary_web_api.error_handling.IllegalArgumentExceptionWithMessageMap;
import com.fejiro.exploration.dictionary.dictionary_web_api.service.language.LanguageDomainObject;
import com.fejiro.exploration.dictionary.dictionary_web_api.service.part_of_speech.PartOfSpeechDomainObject;
import com.fejiro.exploration.dictionary.dictionary_web_api.service.pronunciation.PronunciationDomainObject;
import com.fejiro.exploration.dictionary.dictionary_web_api.service.pronunciation.PronunciationPresignedURLGenerator;
import com.fejiro.exploration.dictionary.dictionary_web_api.service.translation.TranslationDomainObject;
import com.fejiro.exploration.dictionary.dictionary_web_api.service.translation.TranslationService;
import com.fejiro.exploration.dictionary.dictionary_web_api.service.word.WordDomainObject;
import com.fejiro.exploration.dictionary.dictionary_web_api.service.word.WordService;
import com.fejiro.exploration.dictionary.dictionary_web_api.service.word_part.FullWordPartDomainObject;
import com.fejiro.exploration.dictionary.dictionary_web_api.service.word_part.PartWordPartPairDomainObject;
import com.fejiro.exploration.dictionary.dictionary_web_api.service.word_part.WordPartDomainObject;
import com.fejiro.exploration.dictionary.dictionary_web_api.service.word_part.WordPartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/api/words")
public class WordRESTController {

    @Autowired
    WordService wordService;

    @Autowired
    WordPartService wordPartService;

    @Autowired
    TranslationService translationService;

    @Autowired
    WordCreationRequestDTOValidator wordCreationRequestDTOValidator;

    @Autowired
    PronunciationPresignedURLGenerator pronunciationPresignedURLGenerator;

    @DeleteMapping("/{id}")
    ResponseEntity delete(@PathVariable Long id) {
        wordService.deleteById(id);
        return ResponseEntity.ok().build();
    }

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

    @GetMapping("language/{language_id}/nameSearch/full")
    ResponseEntity<Page<FullWordPartDomainObject>> searchByNameFullInLanguage(
            @RequestParam("name") String namePattern,
            @PathVariable("language_id") Integer languageId,
            Pageable pageable) {
        return ResponseEntity.ok(wordService.searchByNameFullInLanguage(namePattern, LanguageDomainObject.builder()
                                                                                                         .id(languageId)
                                                                                                         .build(),
                                                                        pageable));
    }

    /**
     * Update a single word domain model
     *
     * @param wordDomainObject
     * @return
     */
    @PutMapping
    ResponseEntity<WordDomainObject> updateWord(
            @RequestBody WordDomainObject wordDomainObject) throws IllegalArgumentExceptionWithMessageMap {
        return ResponseEntity.ok(wordService.update(wordDomainObject));
    }

    /**
     * Create a word
     *
     * @param creationRequestDTO
     * @return
     */
    @PostMapping
    @Transactional
    public ResponseEntity<WordCreationResultDTO> create(
            @RequestBody WordCreationRequestDTO creationRequestDTO) throws IllegalArgumentExceptionWithMessageMap, ApiExceptionWithComplexObjectMessageMap {
        // Validate creation request
        wordCreationRequestDTOValidator.throwIfInvalid(creationRequestDTO);

        // Create word
        WordDomainObject word = WordDomainObject
                .builder()
                .name(creationRequestDTO.getName())
                .languageId(creationRequestDTO.getTranslationContext().getSource().getId())
                .build();


        WordDomainObject newWord = wordService.create(word);

        // Create word parts
        FullWordPartDomainObject newFullWord = FullWordPartDomainObject.builder()
                                                                       .word(newWord)
                                                                       .parts(createWordParts(newWord,
                                                                                              creationRequestDTO.getParts()))
                                                                       .build();

        // Create translations
        List<TranslationDomainObject> translations = createTranslations(newFullWord.getParts(),
                                                                        creationRequestDTO.getParts());

        // Generate presigned-urls for pronunciation uploads
        List<PronunciationPresignResult> presignResults = presignPronunciations(newFullWord.getParts(),
                                                                                creationRequestDTO.getParts());

        // Generate and return result
        WordCreationResultDTO result = WordCreationResultDTO.builder()
                                                            .word(newFullWord)
                                                            .pronunciationPresignResults(presignResults)
                                                            .translations(translations)
                                                            .build();

        return ResponseEntity.ok(result);
    }

    /**
     * Generate presigned upload URLs for pronunciations in part specifications
     *
     * @param partWordPartPairs
     * @param partSpecificationDTOS
     * @return
     */
    List<PronunciationPresignResult> presignPronunciations(List<PartWordPartPairDomainObject> partWordPartPairs,
                                                           List<WordCreationWordPartSpecificationDTO> partSpecificationDTOS) {
        List<PronunciationPresignResult> presignResults = new ArrayList<>();
        for (var partSpecification : partSpecificationDTOS) {
            WordPartDomainObject sourceWordPart = partWordPartPairs
                    .stream()
                    .filter(partWordPartPairDomainObject -> partWordPartPairDomainObject.getPart()
                                                                                        .getId()
                                                                                        .equals(partSpecification.getPart()
                                                                                                                 .getId()))
                    .findFirst().get()
                    .getWordPart();

            presignResults.addAll(presignPronunciations(sourceWordPart, partSpecification.getPronunciations()));

        }

        return presignResults;
    }

    /**
     * Generate presigned upload urls for pronunciations.
     * Also sets the word part of all pronunciations to sourceWordPart
     *
     * @param sourceWordPart
     * @param pronunciations
     * @return
     */
    List<PronunciationPresignResult> presignPronunciations(WordPartDomainObject sourceWordPart,
                                                           List<PronunciationDomainObject> pronunciations) {

        // Set word part of pronunciation
        pronunciations.forEach(pronunciationDomainObject -> pronunciationDomainObject.setWordPartId(
                sourceWordPart.getId()));
        return pronunciations.stream()
                             .map(pronunciationDomainObject ->
                                          pronunciationPresignedURLGenerator.generatePresignedUploadURL(
                                                  pronunciationDomainObject)
                             )
                             .toList();
    }

    /**
     * Create translation using all the given part specifications and the nested translation specifications
     *
     * @param partWordPartPairs
     * @param partSpecificationDTOS
     * @return
     * @throws IllegalArgumentExceptionWithMessageMap
     * @throws ApiExceptionWithComplexObjectMessageMap
     */
    List<TranslationDomainObject> createTranslations(List<PartWordPartPairDomainObject> partWordPartPairs,
                                                     List<WordCreationWordPartSpecificationDTO> partSpecificationDTOS) throws IllegalArgumentExceptionWithMessageMap, ApiExceptionWithComplexObjectMessageMap {
        List<TranslationDomainObject> translations = new ArrayList<>();
        for (var partSpecification : partSpecificationDTOS) {
            WordPartDomainObject sourceWordPart = partWordPartPairs
                    .stream()
                    .filter(partWordPartPairDomainObject -> partWordPartPairDomainObject.getPart()
                                                                                        .getId()
                                                                                        .equals(partSpecification.getPart()
                                                                                                                 .getId()))
                    .findFirst().get()
                    .getWordPart();

            translations.addAll(createTranslations(sourceWordPart, partSpecification.getTranslations()));
        }

        return translations;
    }

    /**
     * Create translations for a specific source word part
     *
     * @param sourceWordPart
     * @param translationSpecificationDTOS
     * @return
     * @throws IllegalArgumentExceptionWithMessageMap
     * @throws ApiExceptionWithComplexObjectMessageMap
     */
    List<TranslationDomainObject> createTranslations(
            WordPartDomainObject sourceWordPart,
            List<WordCreationTranslationSpecificationDTO> translationSpecificationDTOS) throws IllegalArgumentExceptionWithMessageMap, ApiExceptionWithComplexObjectMessageMap {
        List<TranslationDomainObject> translations = translationSpecificationDTOS
                .stream()
                .map(translationSpecificationDTO -> TranslationDomainObject.builder()
                                                                           .sourceWordPartId(sourceWordPart.getWordId())
                                                                           .targetWordPartId(
                                                                                   translationSpecificationDTO.getWordPart()
                                                                                                              .getId())
                                                                           .note(translationSpecificationDTO.getNote())
                                                                           .build())
                .toList();

        if (translations.isEmpty()) return translations;

        Iterable<TranslationDomainObject> newTranslations = translationService.createAll(translations);

        return StreamSupport.stream(newTranslations.spliterator(), false)
                            .toList();
    }

    /**
     * Create word parts
     *
     * @param word
     * @param partSpecificationDTOS
     * @return
     * @throws IllegalArgumentExceptionWithMessageMap
     * @throws ApiExceptionWithComplexObjectMessageMap
     */
    List<PartWordPartPairDomainObject> createWordParts(WordDomainObject word,
                                                       List<WordCreationWordPartSpecificationDTO> partSpecificationDTOS) throws IllegalArgumentExceptionWithMessageMap, ApiExceptionWithComplexObjectMessageMap {
        List<WordPartDomainObject> wordParts = partSpecificationDTOS.stream()
                                                                    .map(partSpecificationDTO -> generateWordPartCreationModelFromPartSpecification(
                                                                            word,
                                                                            partSpecificationDTO))
                                                                    .toList();

        Iterable<WordPartDomainObject> newWordParts = wordPartService.createAll(wordParts);

        List<PartWordPartPairDomainObject> partWordPartPairs = new ArrayList<>();

        List<PartOfSpeechDomainObject> parts = partSpecificationDTOS.stream()
                                                                    .map(WordCreationWordPartSpecificationDTO::getPart)
                                                                    .toList();

        // Convert newly created word parts into Part-WordPart pair
        for (var newWordPart : newWordParts) {
            PartOfSpeechDomainObject part = parts.stream()
                                                 .filter(partOfSpeechDomainObject -> partOfSpeechDomainObject.getId()
                                                                                                             .equals(newWordPart.getPartId()))
                                                 .findFirst()
                                                 .get();
            PartWordPartPairDomainObject pair = PartWordPartPairDomainObject.builder()
                                                                            .wordPart(newWordPart)
                                                                            .part(part)
                                                                            .build();
            partWordPartPairs.add(pair);
        }

        return partWordPartPairs;
    }

    private static WordPartDomainObject generateWordPartCreationModelFromPartSpecification(WordDomainObject word,
                                                                                           WordCreationWordPartSpecificationDTO partSpecificationDTO) {
        return WordPartDomainObject.builder()
                                   .wordId(word.getId())
                                   .partId(partSpecificationDTO.getPart()
                                                               .getId())
                                   .note(partSpecificationDTO.getNote())
                                   .definition(
                                           partSpecificationDTO.getDefinition())
                                   .build();
    }
}
