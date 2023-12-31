package com.fejiro.exploration.dictionary.dictionary_web_api.security;


import lombok.*;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PermissionDomainObject {
    @EqualsAndHashCode.Include
    Integer id;

    @EqualsAndHashCode.Include
    @NonNull
    String name;

    String description;
}
