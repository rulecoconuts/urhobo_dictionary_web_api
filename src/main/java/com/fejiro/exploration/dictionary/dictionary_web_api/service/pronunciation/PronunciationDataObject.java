package com.fejiro.exploration.dictionary.dictionary_web_api.service.pronunciation;

import com.fejiro.exploration.dictionary.dictionary_web_api.database.TemporalAuditable;
import com.fejiro.exploration.dictionary.dictionary_web_api.database.UserAuditable;
import lombok.*;

import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PronunciationDataObject implements TemporalAuditable, UserAuditable {
    @EqualsAndHashCode.Include
    Long id;

    String phoneticSpelling;
    String audioUrl;

    Long audioByteSize;

    String audioFileType;

    Integer audioMillisecondDuration;

    Long wordPartId;

    OffsetDateTime createdAt;
    OffsetDateTime updatedAt;

    Integer createdBy;
    Integer updatedBy;
}
