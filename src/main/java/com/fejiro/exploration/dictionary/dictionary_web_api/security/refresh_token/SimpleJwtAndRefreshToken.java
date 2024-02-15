package com.fejiro.exploration.dictionary.dictionary_web_api.security.refresh_token;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SimpleJwtAndRefreshToken {
    final String token;
    final RefreshTokenDomainObject refreshToken;
}
