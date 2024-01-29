package com.fejiro.exploration.dictionary.dictionary_web_api.service.user;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class AppUserDataObjectToAppUserDomainObjectConverter implements Converter<AppUserDataObject, AppUserDomainObject> {
    @Override
    public AppUserDomainObject convert(AppUserDataObject source) {
        return AppUserDomainObject.builder()
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
