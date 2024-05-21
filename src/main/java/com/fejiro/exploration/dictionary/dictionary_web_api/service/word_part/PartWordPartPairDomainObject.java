package com.fejiro.exploration.dictionary.dictionary_web_api.service.word_part;

import com.fejiro.exploration.dictionary.dictionary_web_api.service.part_of_speech.PartOfSpeechDomainObject;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PartWordPartPairDomainObject {
    WordPartDomainObject wordPart;
    PartOfSpeechDomainObject part;
}
