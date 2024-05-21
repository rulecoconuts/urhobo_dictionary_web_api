package com.fejiro.exploration.dictionary.dictionary_web_api.data_transfer.word;

import com.fejiro.exploration.dictionary.dictionary_web_api.service.part_of_speech.PartOfSpeechDomainObject;
import com.fejiro.exploration.dictionary.dictionary_web_api.service.pronunciation.PronunciationDomainObject;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class WordCreationWordPartSpecificationDTO implements Serializable {
    PartOfSpeechDomainObject part;
    String definition;
    String note;

    List<PronunciationDomainObject> pronunciations = new ArrayList<>();
    List<WordCreationTranslationSpecificationDTO> translations = new ArrayList<>();
}
