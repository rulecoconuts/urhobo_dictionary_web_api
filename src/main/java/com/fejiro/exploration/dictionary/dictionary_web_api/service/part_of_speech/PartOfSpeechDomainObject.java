package com.fejiro.exploration.dictionary.dictionary_web_api.service.part_of_speech;

import com.fejiro.exploration.dictionary.dictionary_web_api.database.TemporalAuditable;
import com.fejiro.exploration.dictionary.dictionary_web_api.database.UserAuditable;
import lombok.*;

import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PartOfSpeechDomainObject implements TemporalAuditable, UserAuditable {
    @EqualsAndHashCode.Include
    Integer id;
    
    @EqualsAndHashCode.Include
    String name;
    String description;
    OffsetDateTime createdAt;
    OffsetDateTime updatedAt;

    Integer createdBy;
    Integer updatedBy;
}
