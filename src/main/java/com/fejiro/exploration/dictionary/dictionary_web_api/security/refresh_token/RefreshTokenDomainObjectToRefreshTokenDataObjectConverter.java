package com.fejiro.exploration.dictionary.dictionary_web_api.security.refresh_token;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class RefreshTokenDomainObjectToRefreshTokenDataObjectConverter implements Converter<RefreshTokenDomainObject, RefreshTokenDataObject> {
    @Override
    public RefreshTokenDataObject convert(RefreshTokenDomainObject source) {
        return RefreshTokenDataObject.builder()
                                     .id(source.getId())
                                     .userId(source.getUserId())
                                     .content(source.getContent())
                                     .expiryDate(source.getExpiryDate())
                                     .enabled(source.isEnabled())
                                     .updatedAt(source.getUpdatedAt())
                                     .createdAt(source.getCreatedAt())
                                     .build();
    }
}
