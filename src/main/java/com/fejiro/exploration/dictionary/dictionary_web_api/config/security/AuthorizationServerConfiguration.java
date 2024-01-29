package com.fejiro.exploration.dictionary.dictionary_web_api.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.UUID;

@Configuration
public class AuthorizationServerConfiguration {

    /**
     * Configure OAuth2 clients
     *
     * @param clientConfigurationProperties
     * @return
     */
    @Bean
    RegisteredClientRepository registeredClientRepository(
            OidcClientConfigurationProperties clientConfigurationProperties) {
        RegisteredClient client = RegisteredClient.withId(UUID.randomUUID().toString())
                                                  .clientId(
                                                          clientConfigurationProperties.getClientId())
                                                  .clientSecret(
                                                          clientConfigurationProperties.getClientSecret())
                                                  .clientAuthenticationMethod(
                                                          ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                                                  .authorizationGrantType(
                                                          AuthorizationGrantType.AUTHORIZATION_CODE)
                                                  .authorizationGrantType(
                                                          AuthorizationGrantType.REFRESH_TOKEN)
                                                  .build();

        return new InMemoryRegisteredClientRepository(client);
    }

    /**
     * Configure authorization filter chain
     *
     * @param http
     * @return
     */
    @Bean
    @Order(1)
    SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http); // apply default config for oauth2

        http.getConfigurer(OAuth2AuthorizationServerConfigurer.class)
            .oidc(Customizer.withDefaults()); // Enable Open ID Connect 1.0

        // Enable oauth2 resource server protocol so endpoints can be protected using
        // oauth2 jwt access token
        http.oauth2ResourceServer((oauth) -> oauth.jwt(Customizer.withDefaults()));

        return http.cors(Customizer.withDefaults()).build();
    }

    @Bean
    @Order(1)
    SecurityFilterChain defaultFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests((authorize) -> authorize.anyRequest().authenticated())
            .oauth2Login(Customizer.withDefaults())
            .httpBasic(Customizer.withDefaults());

        return http.build();
    }

    /**
     * Setup Cross-Origin configuration
     *
     * @return
     */
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource configurationSource = new UrlBasedCorsConfigurationSource();
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
        configuration.addAllowedOrigin("*");
        configuration.setAllowCredentials(true);
        configurationSource.registerCorsConfiguration("/**", configuration);

        return configurationSource;
    }

}
