package com.fejiro.exploration.dictionary.dictionary_web_api.security.jwt;

import com.fejiro.exploration.dictionary.dictionary_web_api.security.CustomSecurityUserDetails;
import com.fejiro.exploration.dictionary.dictionary_web_api.service.user.AppUserDomainObject;
import com.fejiro.exploration.dictionary.dictionary_web_api.service.user.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

@Component
public class JwtGenerator {

    @Autowired
    JwtConfigProperties jwtConfigProperties;

    @Autowired
    UserService userService;

    String generateToken(Authentication authentication) {
        CustomSecurityUserDetails userDetails = (CustomSecurityUserDetails) authentication.getPrincipal();
        OffsetDateTime issuedAt = OffsetDateTime.now();
        OffsetDateTime expirationDate = getExpirationDate(issuedAt);

        return Jwts.builder()
                   .signWith(SignatureAlgorithm.HS512, jwtConfigProperties.getSecret())
                   .setSubject(userDetails.getUserDomainObject().getId().toString())
                   .setAudience(jwtConfigProperties.getAudience())
                   .setIssuer(jwtConfigProperties.getIssuer())
                   .setIssuedAt(Date.from(issuedAt.toInstant()))
                   .setExpiration(Date.from(expirationDate.toInstant()))
                   .setHeaderParam("typ", jwtConfigProperties.getType())
                   .compact();
    }

    OffsetDateTime getExpirationDate(OffsetDateTime issuedAt) {
        Duration duration = Duration.parse(jwtConfigProperties.getLifeTime());

        return issuedAt.plus(duration);
    }

    Authentication parseToken(String token) {
        Jws<Claims> claims = Jwts.parser()
                                 .setSigningKey(jwtConfigProperties.getSecret().getBytes())
                                 .build().parseSignedClaims(token);
        Integer id = Integer.parseInt(claims.getBody().getSubject());

        Optional<AppUserDomainObject> user = userService.retrieveById(id);

        if (user.isEmpty()) return null;

        return new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());
    }
}
