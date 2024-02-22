package com.fejiro.exploration.dictionary.dictionary_web_api.service.word;

import lombok.*;

import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class WordDomainObject {
    @EqualsAndHashCode.Include
    Long id;

    @EqualsAndHashCode.Include
    String name;

    @EqualsAndHashCode.Include
    Integer languageId;

    OffsetDateTime createdAt;
    OffsetDateTime updatedAt;

    Integer createdBy;
    Integer updatedBy;
}
