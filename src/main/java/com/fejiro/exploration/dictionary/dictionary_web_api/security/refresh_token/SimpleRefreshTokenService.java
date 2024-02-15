package com.fejiro.exploration.dictionary.dictionary_web_api.security.refresh_token;

import com.fejiro.exploration.dictionary.dictionary_web_api.database.CRUDDAO;
import com.fejiro.exploration.dictionary.dictionary_web_api.database.GenericJOOQCRUDDAO;
import com.fejiro.exploration.dictionary.dictionary_web_api.error_handling.IllegalArgumentExceptionWithMessageMap;
import com.fejiro.exploration.dictionary.dictionary_web_api.security.jwt.JwtGenerator;
import com.fejiro.exploration.dictionary.dictionary_web_api.service.CRUDService;
import com.fejiro.exploration.dictionary.dictionary_web_api.service.ConversionServiceDataBackedService;
import com.fejiro.exploration.dictionary.dictionary_web_api.service.DeletionService;
import com.fejiro.exploration.dictionary.dictionary_web_api.service.RetrievalService;
import com.fejiro.exploration.dictionary.dictionary_web_api.service.user.AppUserDataObject;
import com.fejiro.exploration.dictionary.dictionary_web_api.tables.RefreshToken;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Optional;

@Component
public class SimpleRefreshTokenService implements RefreshTokenService<String>, ConversionServiceDataBackedService<RefreshTokenDomainObject, RefreshTokenDataObject>,
        CRUDService<RefreshTokenDomainObject, Long> {
    @Autowired
    ConversionService conversionService;

    @Autowired
    CRUDDAO<RefreshTokenDataObject, Long> refreshTokenCRUDDAO;

    @Autowired
    JwtGenerator jwtGenerator;

    /**
     * Generate refresh token from token
     *
     * @param token
     * @return
     * @throws IllegalArgumentExceptionWithMessageMap
     */
    @Override
    public RefreshTokenDomainObject generate(String token) throws IllegalArgumentExceptionWithMessageMap {
        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        String refreshTokenContent = encoder.encode(token);
        Jws<Claims> claims = jwtGenerator.parseToken(token);
        OffsetDateTime expiryDate = jwtGenerator.getExpiryDate(claims);
        Integer userId = jwtGenerator.getUserId(claims);

        RefreshTokenDomainObject refreshToken = RefreshTokenDomainObject.builder()
                                                                        .content(refreshTokenContent)
                                                                        .enabled(true)
                                                                        .expiryDate(expiryDate)
                                                                        .userId(userId)
                                                                        .build();
        return create(refreshToken);
    }

    @Override
    public boolean matches(String token, RefreshTokenDomainObject refreshToken) {
        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        return encoder.matches(token, refreshToken.getContent());
    }

    @Override
    public void deleteMatchingToken(String token) {
        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        String refreshTokenContent = encoder.encode(token);
        Integer userId = jwtGenerator.getUserId(token);

        RefreshTokenDomainObject refreshToken = RefreshTokenDomainObject.builder()
                                                                        .content(refreshTokenContent)
                                                                        .enabled(true)
                                                                        .userId(userId)
                                                                        .build();

        deleteMatch(refreshToken);
    }

    @Override
    public void deleteMatch(RefreshTokenDomainObject refreshToken) {
        (((GenericJOOQCRUDDAO<RefreshTokenDataObject, Long, ?>) refreshTokenCRUDDAO))
                .delete(RefreshToken.REFRESH_TOKEN.CONTENT.eq(refreshToken.getContent())
                                                          .and(RefreshToken.REFRESH_TOKEN.USER_ID.eq(
                                                                  refreshToken.getUserId())));
    }

    @Override
    public RefreshTokenDomainObject create(
            RefreshTokenDomainObject model) throws IllegalArgumentExceptionWithMessageMap {
        throwIfModelIsInvalidForCreation(model);
        RefreshTokenDataObject dataObject = toData(model);

        return toDomain(refreshTokenCRUDDAO.create(dataObject));
    }

    @Override
    public Iterable<RefreshTokenDomainObject> createAll(
            Iterable<RefreshTokenDomainObject> models) throws IllegalArgumentExceptionWithMessageMap {
        return null;
    }

    @Override
    public Map<String, String> validateModelForCreation(RefreshTokenDomainObject model) {
        return null;
    }

    @Override
    public void throwIfModelIsInvalidForCreation(
            RefreshTokenDomainObject model) throws IllegalArgumentExceptionWithMessageMap {

    }

    @Override
    public ConversionService getConversionService() {
        return conversionService;
    }

    @Override
    public Class<? extends RefreshTokenDomainObject> getDomainClass() {
        return RefreshTokenDomainObject.class;
    }

    @Override
    public Class<? extends RefreshTokenDataObject> getDataClass() {
        return RefreshTokenDataObject.class;
    }

    @Override
    public void delete(RefreshTokenDomainObject model) {

    }

    @Override
    public void deleteById(Long id) {

    }

    @Override
    public void deleteAll(Iterable<RefreshTokenDomainObject> models) {

    }

    @Override
    public void deleteAllById(Iterable<Long> ids) {

    }

    @Override
    public Optional<RefreshTokenDomainObject> retrieveById(Long id) {
        return Optional.empty();
    }

    @Override
    public Iterable<RefreshTokenDomainObject> retrieveAll() {
        return null;
    }

    @Override
    public Iterable<RefreshTokenDomainObject> retrieveAllById(Iterable<Long> ids) {
        return null;
    }

    @Override
    public RefreshTokenDomainObject update(RefreshTokenDomainObject model) {
        return null;
    }

    @Override
    public Iterable<RefreshTokenDomainObject> updateAll(Iterable<RefreshTokenDomainObject> models) {
        return null;
    }

    @Override
    public Map<String, String> validateModelForUpdate(RefreshTokenDomainObject model) {
        return null;
    }

    @Override
    public void throwIfModelIsInvalidForUpdate(
            RefreshTokenDomainObject model) throws IllegalArgumentExceptionWithMessageMap {

    }
}
