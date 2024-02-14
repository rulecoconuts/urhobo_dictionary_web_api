package com.fejiro.exploration.dictionary.dictionary_web_api.database;

import java.time.OffsetDateTime;

public interface TemporalAuditable {
    OffsetDateTime getCreatedAt();

    void setCreatedAt(OffsetDateTime dateTime);

    OffsetDateTime getUpdatedAt();

    void setUpdatedAt(OffsetDateTime dateTime);
}
