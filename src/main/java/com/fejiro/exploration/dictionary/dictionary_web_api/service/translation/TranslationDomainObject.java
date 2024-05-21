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

    /**
     * Checks to see if this translation is equal to another.
     * The order of source and target are ignored
     *
     * @param other
     * @return
     */
    public boolean equalUnordered(TranslationDomainObject other) {
        boolean normal = sourceWordPartId.equals(other.sourceWordPartId) && targetWordPartId.equals(
                other.targetWordPartId);
        boolean flipped = sourceWordPartId.equals(other.targetWordPartId) && targetWordPartId.equals(
                other.sourceWordPartId);

        return normal || flipped;
    }
}
