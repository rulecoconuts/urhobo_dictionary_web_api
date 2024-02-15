package com.fejiro.exploration.dictionary.dictionary_web_api.security.refresh_token;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class RefreshTokenDataObjectToRefreshTokenDomainObject implements Converter<RefreshTokenDataObject, RefreshTokenDomainObject> {
    @Override
    public RefreshTokenDomainObject convert(RefreshTokenDataObject source) {
        return RefreshTokenDomainObject.builder()
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
