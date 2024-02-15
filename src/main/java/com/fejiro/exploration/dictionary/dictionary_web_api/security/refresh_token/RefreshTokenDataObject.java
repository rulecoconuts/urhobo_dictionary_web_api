package com.fejiro.exploration.dictionary.dictionary_web_api.security.refresh_token;

import com.fejiro.exploration.dictionary.dictionary_web_api.database.TemporalAuditable;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.OffsetDateTime;

@Data
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class RefreshTokenDataObject implements TemporalAuditable {
    @EqualsAndHashCode.Include
    Long id;
    Integer userId;
    String content;
    boolean enabled;
    OffsetDateTime expiryDate;
    OffsetDateTime createdAt;
    OffsetDateTime updatedAt;
}
