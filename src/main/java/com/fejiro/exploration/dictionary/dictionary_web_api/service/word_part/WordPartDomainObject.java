package com.fejiro.exploration.dictionary.dictionary_web_api.service.word_part;

import com.fejiro.exploration.dictionary.dictionary_web_api.database.TemporalAuditable;
import com.fejiro.exploration.dictionary.dictionary_web_api.database.UserAuditable;
import lombok.*;

import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class WordPartDomainObject implements TemporalAuditable, UserAuditable {
    @EqualsAndHashCode.Include
    Long id;

    @EqualsAndHashCode.Include
    Long wordId;

    @EqualsAndHashCode.Include
    Integer partId;

    String definition;

    String note;


    OffsetDateTime createdAt;
    OffsetDateTime updatedAt;

    Integer createdBy;
    Integer updatedBy;
}
