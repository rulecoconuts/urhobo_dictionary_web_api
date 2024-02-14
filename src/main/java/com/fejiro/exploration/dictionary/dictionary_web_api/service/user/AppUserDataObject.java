package com.fejiro.exploration.dictionary.dictionary_web_api.service.user;

import com.fejiro.exploration.dictionary.dictionary_web_api.database.TemporalAuditable;
import lombok.*;

import java.time.OffsetDateTime;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class AppUserDataObject implements TemporalAuditable {
    @EqualsAndHashCode.Include
    Integer id;

    String username;

    @EqualsAndHashCode.Include
    String email;

    String firstName;

    String lastName;

    String password;

    OffsetDateTime createdAt;

    OffsetDateTime updatedAt;
}
