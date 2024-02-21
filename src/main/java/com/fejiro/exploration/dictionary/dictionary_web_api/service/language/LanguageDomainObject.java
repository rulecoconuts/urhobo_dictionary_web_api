package com.fejiro.exploration.dictionary.dictionary_web_api.service.language;

import com.fejiro.exploration.dictionary.dictionary_web_api.database.TemporalAuditable;
import com.fejiro.exploration.dictionary.dictionary_web_api.database.UserAuditable;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.OffsetDateTime;

@Builder(toBuilder = true)
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class LanguageDomainObject implements UserAuditable, TemporalAuditable {
    @EqualsAndHashCode.Include
    Integer id;

    @EqualsAndHashCode.Include
    String name;

    String description;
    Integer createdBy;
    Integer updatedBy;
    OffsetDateTime createdAt;
    OffsetDateTime updatedAt;
}
