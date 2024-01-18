package com.fejiro.exploration.dictionary.dictionary_web_api.service.user;

import com.fejiro.exploration.dictionary.dictionary_web_api.database.TemporalAuditable;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.OffsetDateTime;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class AppUserDataObject implements TemporalAuditable {
    @EqualsAndHashCode.Include
    Integer id;

    @EqualsAndHashCode.Include
    String username;

    @EqualsAndHashCode.Include
    String email;

    String firstName;

    String lastName;

    String password;

    OffsetDateTime createdAt;

    OffsetDateTime updatedAt;
}
