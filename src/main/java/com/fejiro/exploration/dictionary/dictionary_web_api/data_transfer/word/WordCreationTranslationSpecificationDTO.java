package com.fejiro.exploration.dictionary.dictionary_web_api.data_transfer.word;

import com.fejiro.exploration.dictionary.dictionary_web_api.service.word_part.WordPartDomainObject;
import lombok.Data;

import java.io.Serializable;

@Data
public class WordCreationTranslationSpecificationDTO implements Serializable {
    String note;

    WordPartDomainObject wordPart;
}
