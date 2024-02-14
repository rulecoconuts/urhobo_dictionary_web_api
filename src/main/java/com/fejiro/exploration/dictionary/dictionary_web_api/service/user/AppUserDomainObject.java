package com.fejiro.exploration.dictionary.dictionary_web_api.service.user;

import com.fejiro.exploration.dictionary.dictionary_web_api.database.TemporalAuditable;
import com.fejiro.exploration.dictionary.dictionary_web_api.service.clone.ShallowCloneable;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.OffsetDateTime;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder(toBuilder = true)
public class AppUserDomainObject implements TemporalAuditable, ShallowCloneable<AppUserDomainObject> {
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

    public AppUserDomainObject shallowClone() {
        return toBuilder().build();
    }
}
