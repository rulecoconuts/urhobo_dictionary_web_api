package com.fejiro.exploration.dictionary.dictionary_web_api.database;

import java.time.OffsetDateTime;

public interface UserAuditable {
    Integer getCreatedBy();

    void setCreatedBy(Integer userId);

    Integer getUpdatedBy();

    void setUpdatedBy(Integer userId);
}
