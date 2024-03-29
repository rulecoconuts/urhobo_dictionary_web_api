package com.fejiro.exploration.dictionary.dictionary_web_api.service.language;

import com.fejiro.exploration.dictionary.dictionary_web_api.database.TemporalAuditable;
import com.fejiro.exploration.dictionary.dictionary_web_api.database.UserAuditable;
import com.fejiro.exploration.dictionary.dictionary_web_api.service.user.AppUserDataObject;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.OffsetDateTime;

@Builder
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class LanguageDataObject implements UserAuditable, TemporalAuditable {
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
