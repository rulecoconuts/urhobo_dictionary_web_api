package com.fejiro.exploration.dictionary.dictionary_web_api.security.jwt;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtConfigProperties {
    String issuer;
    String audience;
    String type;
    String secret;
    String lifeTime;
}
