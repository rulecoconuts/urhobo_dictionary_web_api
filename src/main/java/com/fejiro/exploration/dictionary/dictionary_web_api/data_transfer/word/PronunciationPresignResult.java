package com.fejiro.exploration.dictionary.dictionary_web_api.data_transfer.word;

import com.fejiro.exploration.dictionary.dictionary_web_api.service.pronunciation.PronunciationDomainObject;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class PronunciationPresignResult implements Serializable {
    PronunciationDomainObject pronunciation;
    String presignedUrl;
}
