package com.fejiro.exploration.dictionary.dictionary_web_api.service.translation;

import com.fejiro.exploration.dictionary.dictionary_web_api.database.TemporalAuditable;
import com.fejiro.exploration.dictionary.dictionary_web_api.database.UserAuditable;
import lombok.*;

import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class TranslationDomainObject implements TemporalAuditable, UserAuditable {
    @EqualsAndHashCode.Include
    Long id;

    @EqualsAndHashCode.Include
    Long sourceWordPartId;

    @EqualsAndHashCode.Include
    Long targetWordPartId;
    String note;
    String reverseNote;

    OffsetDateTime createdAt;
    OffsetDateTime updatedAt;

    Integer createdBy;
    Integer updatedBy;
}
