package com.fejiro.exploration.dictionary.dictionary_web_api.security.refresh_token;

import com.fejiro.exploration.dictionary.dictionary_web_api.error_handling.IllegalArgumentExceptionWithMessageMap;

public interface RefreshTokenService<T> {

    /**
     * Generate refresh token from token
     *
     * @param token
     * @return
     */
    RefreshTokenDomainObject generate(T token) throws IllegalArgumentExceptionWithMessageMap;

    /**
     * Check if the refreshToken is a match for the provided token
     *
     * @param token
     * @param refreshToken
     * @return
     */
    boolean matches(T token, RefreshTokenDomainObject refreshToken);

    void deleteMatchingToken(T token);

    void deleteMatch(RefreshTokenDomainObject refreshToken);
}
