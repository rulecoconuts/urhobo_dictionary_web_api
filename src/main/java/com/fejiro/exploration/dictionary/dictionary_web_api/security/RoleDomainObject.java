package com.fejiro.exploration.dictionary.dictionary_web_api.security;

import com.fejiro.exploration.dictionary.dictionary_web_api.database.SimpleUserAndTemporalAuditable;
import lombok.*;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class RoleDomainObject extends SimpleUserAndTemporalAuditable {
    @EqualsAndHashCode.Include
    Integer id;

    @EqualsAndHashCode.Include
    @NonNull
    String name;

    String description;
}
