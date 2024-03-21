package com.fejiro.exploration.dictionary.dictionary_web_api.data_transfer.word;

import com.fejiro.exploration.dictionary.dictionary_web_api.service.translation.TranslationDomainObject;
import com.fejiro.exploration.dictionary.dictionary_web_api.service.word_part.FullWordPartDomainObject;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class WordCreationResultDTO implements Serializable {
    FullWordPartDomainObject word;

    @Singular
    List<PronunciationPresignResult> pronunciationPresignResults = new ArrayList<>();

    @Singular
    List<TranslationDomainObject> translations = new ArrayList<>();
}
