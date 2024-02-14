package com.fejiro.exploration.dictionary.dictionary_web_api.security.data;


import lombok.*;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PermissionDataObject {
    @EqualsAndHashCode.Include
    Integer id;

    @EqualsAndHashCode.Include
    @NonNull
    String name;

    String description;
}
