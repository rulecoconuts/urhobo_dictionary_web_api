package com.fejiro.exploration.dictionary.dictionary_web_api.service.word;

import com.fejiro.exploration.dictionary.dictionary_web_api.database.TemporalAuditable;
import com.fejiro.exploration.dictionary.dictionary_web_api.database.UserAuditable;
import lombok.*;

import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class WordDataObject implements TemporalAuditable, UserAuditable {
    @EqualsAndHashCode.Include
    Long id;

    @EqualsAndHashCode.Include
    String name;

    Integer languageId;

    OffsetDateTime createdAt;
    OffsetDateTime updatedAt;

    Integer createdBy;
    Integer updatedBy;
}
