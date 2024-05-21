package com.fejiro.exploration.dictionary.dictionary_web_api.service.word_part;

import com.fejiro.exploration.dictionary.dictionary_web_api.service.part_of_speech.PartOfSpeechDomainObject;
import com.fejiro.exploration.dictionary.dictionary_web_api.service.word.WordDomainObject;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class FullWordPartDomainObject {
    WordDomainObject word;

    @Singular
    List<PartWordPartPairDomainObject> parts = new ArrayList<>();
}
