package com.fejiro.exploration.dictionary.dictionary_web_api.database;

import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
public class SimpleUserAndTemporalAuditable implements TemporalAuditable, UserAuditable {
    OffsetDateTime createdAt;
    OffsetDateTime updatedAt;

    Integer createdBy;

    Integer updatedBy;
}
