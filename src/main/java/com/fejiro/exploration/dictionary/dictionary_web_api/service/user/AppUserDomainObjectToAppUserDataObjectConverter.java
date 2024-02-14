package com.fejiro.exploration.dictionary.dictionary_web_api.service.user;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class AppUserDomainObjectToAppUserDataObjectConverter implements Converter<AppUserDomainObject, AppUserDataObject> {
    @Override
    public AppUserDataObject convert(AppUserDomainObject source) {
        return AppUserDataObject.builder()
                                .id(source.getId())
                                .username(source.getUsername())
                                .firstName(source.getFirstName())
                                .lastName(source.getLastName())
                                .email(source.getEmail())
                                .createdAt(source.getCreatedAt())
                                .updatedAt(source.getUpdatedAt())
                                .password(source.getPassword())
                                .build();
    }
}
