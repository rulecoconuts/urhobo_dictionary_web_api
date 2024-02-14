package com.fejiro.exploration.dictionary.dictionary_web_api.config.security;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "default-oidc-client")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OidcClientConfigurationProperties {
    private String clientId;

    private String clientSecret;
}
