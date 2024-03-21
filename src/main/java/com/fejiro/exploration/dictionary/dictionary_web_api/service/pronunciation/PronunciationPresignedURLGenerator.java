package com.fejiro.exploration.dictionary.dictionary_web_api.service.pronunciation;

public interface PronunciationPresignedURLGenerator {
    String generatePresignedUploadURL(PronunciationDomainObject pronunciation);
}
