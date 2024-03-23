package com.fejiro.exploration.dictionary.dictionary_web_api.controller.rest.pronunciation;

import com.fejiro.exploration.dictionary.dictionary_web_api.error_handling.ApiExceptionWithComplexObjectMessageMap;
import com.fejiro.exploration.dictionary.dictionary_web_api.error_handling.IllegalArgumentExceptionWithMessageMap;
import com.fejiro.exploration.dictionary.dictionary_web_api.service.pronunciation.PronunciationDomainObject;
import com.fejiro.exploration.dictionary.dictionary_web_api.service.pronunciation.PronunciationPresignedURLGenerator;
import com.fejiro.exploration.dictionary.dictionary_web_api.service.pronunciation.PronunciationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.util.List;

@RestController
@RequestMapping("/api/pronunciations")
public class PronunciationRESTController {

    @Autowired
    PronunciationPresignedURLGenerator pronunciationPresignedURLGenerator;

    @Autowired
    PronunciationService pronunciationService;

    /**
     * Generate a presigned url to allow upload of a pronunciation recording
     *
     * @param pronunciationDomainObject
     * @return
     */
    @PostMapping("/presigned-upload-url")
    ResponseEntity generatePresignedUrl(@RequestBody PronunciationDomainObject pronunciationDomainObject) {
        return ResponseEntity.ok(
                pronunciationPresignedURLGenerator.generatePresignedUploadURL(pronunciationDomainObject));
    }

    /**
     * Create a pronunciation record
     *
     * @param pronunciationDomainObject
     * @return
     */
    @PostMapping
    ResponseEntity create(
            @RequestBody PronunciationDomainObject pronunciationDomainObject) throws IllegalArgumentExceptionWithMessageMap {
        return ResponseEntity.ok(pronunciationService.create(pronunciationDomainObject));
    }

    /**
     * Create multiple pronunciations records
     *
     * @param pronunciations
     * @return
     * @throws IllegalArgumentExceptionWithMessageMap
     * @throws ApiExceptionWithComplexObjectMessageMap
     */
    @PostMapping("/bulk")
    ResponseEntity createAll(
            @RequestBody List<PronunciationDomainObject> pronunciations) throws IllegalArgumentExceptionWithMessageMap, ApiExceptionWithComplexObjectMessageMap {
        return ResponseEntity.ok(pronunciationService.createAll(pronunciations));
    }
}
