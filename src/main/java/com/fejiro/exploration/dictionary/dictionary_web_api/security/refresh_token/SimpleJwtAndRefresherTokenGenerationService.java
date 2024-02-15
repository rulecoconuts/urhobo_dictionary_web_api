package com.fejiro.exploration.dictionary.dictionary_web_api.security.refresh_token;

import com.fejiro.exploration.dictionary.dictionary_web_api.error_handling.IllegalArgumentExceptionWithMessageMap;
import com.fejiro.exploration.dictionary.dictionary_web_api.security.CustomSecurityUserDetails;
import com.fejiro.exploration.dictionary.dictionary_web_api.security.jwt.JwtGenerator;
import com.fejiro.exploration.dictionary.dictionary_web_api.service.user.AppUserDomainObject;
import com.fejiro.exploration.dictionary.dictionary_web_api.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Optional;

@Component
public class SimpleJwtAndRefresherTokenGenerationService {

    @Autowired
    RefreshTokenService<String> refreshTokenService;

    @Autowired
    JwtGenerator jwtGenerator;

    @Autowired
    UserService userService;

    /**
     * Generate a pair of JWT and a matching refresh token
     *
     * @param authentication
     * @return
     */
    public SimpleJwtAndRefreshToken generate(
            Authentication authentication) throws IllegalArgumentExceptionWithMessageMap {
        String token = jwtGenerator.generateToken(authentication);

        RefreshTokenDomainObject refreshToken = refreshTokenService.generate(token);

        return new SimpleJwtAndRefreshToken(token, refreshToken);
    }

    public SimpleJwtAndRefreshToken refresh(String token,
                                            String refreshTokenContent) throws IllegalArgumentExceptionWithMessageMap {
        Integer userId = jwtGenerator.getUserId(token, false);
        RefreshTokenDomainObject refreshToken = RefreshTokenDomainObject.builder()
                                                                        .content(refreshTokenContent)
                                                                        .userId(userId)
                                                                        .build();

        boolean isAMatch = refreshTokenService.matches(token, refreshToken);

        // If token does not match refresh token throw an error
        if (!isAMatch) throw new BadCredentialsException("Invalid refresh token");

        // If token matches refresh token.
        // Delete refresh token
        refreshTokenService.deleteMatch(refreshToken);

        // Generate new token and refresh token pair
        Optional<AppUserDomainObject> user = userService.retrieveById(userId);

        if (user.isEmpty()) throw new BadCredentialsException("User does not exist");

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                new CustomSecurityUserDetails(user.get().getEmail(), user.get().getPassword(), new ArrayList<>(),
                                              user.get()), null);

        return generate(authentication);
    }
}
