package com.fejiro.exploration.dictionary.dictionary_web_api.service.pronunciation;

import com.fejiro.exploration.dictionary.dictionary_web_api.data_transfer.word.PronunciationPresignResult;

public interface PronunciationPresignedURLGenerator {
    PronunciationPresignResult generatePresignedUploadURL(PronunciationDomainObject pronunciation);
}
