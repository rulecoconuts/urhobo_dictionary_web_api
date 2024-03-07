package com.fejiro.exploration.dictionary.dictionary_web_api.config.security;

import com.fejiro.exploration.dictionary.dictionary_web_api.security.FilterChainExceptionHandler;
import com.fejiro.exploration.dictionary.dictionary_web_api.security.RESTExceptionHandlerBackedAuthenticationFailureHandler;
import com.fejiro.exploration.dictionary.dictionary_web_api.security.jwt.JwtAuthenticationFilter;
import com.fejiro.exploration.dictionary.dictionary_web_api.security.jwt.JwtAuthorizationFilter;
import com.fejiro.exploration.dictionary.dictionary_web_api.security.jwt.JwtGenerator;
import com.fejiro.exploration.dictionary.dictionary_web_api.security.refresh_token.SimpleJwtAndRefresherTokenGenerationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityFilterChainConfiguration {

    @Autowired
    FilterChainExceptionHandler filterChainExceptionHandler;

    @Autowired
    RESTExceptionHandlerBackedAuthenticationFailureHandler authenticationFailureHandler;

    @Autowired
    SimpleJwtAndRefresherTokenGenerationService jwtAndRefresherTokenGenerationService;


    @Bean
    @Order(1)
    SecurityFilterChain authenticationFilterChain(HttpSecurity http, JwtGenerator jwtGenerator,
                                                  AuthenticationConfiguration authenticationConfiguration) throws Exception {
        AuthenticationManager authenticationManager = authenticationConfiguration.getAuthenticationManager();

        return http
                .securityMatcher("/api/login/**")
                .authorizeHttpRequests(authorize ->
                                               authorize.requestMatchers("/api/login/**")
                                                        .anonymous())
                .addFilterAfter(filterChainExceptionHandler, LogoutFilter.class)
                .addFilter(
                        new JwtAuthenticationFilter(authenticationManager, jwtAndRefresherTokenGenerationService,
                                                    authenticationFailureHandler))
                .sessionManagement(
                        sessionManager -> sessionManager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .cors(cors ->
                              cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .build();
    }

    @Bean
    @Order(2)
    SecurityFilterChain anonymousFilterChain(HttpSecurity http) throws Exception {
        return http
                .securityMatcher("/api/users/register/**", "/api/users/refresh/**", "/health-check")
                .authorizeHttpRequests(
                        authorize -> authorize.requestMatchers("/api/users/register/**", "/api/users/refresh/**",
                                                               "/health-check")
                                              .permitAll())
                .anonymous(Customizer.withDefaults())
                .sessionManagement(
                        sessionManager -> sessionManager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .cors(cors ->
                              cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .build();
    }

    @Bean
    @Order(3)
    SecurityFilterChain defaultFilterChain(HttpSecurity http, JwtGenerator jwtGenerator,
                                           AuthenticationConfiguration authenticationConfiguration,
                                           ApplicationContext applicationContext
    ) throws Exception {
        AuthenticationManager authenticationManager = authenticationConfiguration.getAuthenticationManager();

        return http
                .addFilterBefore(filterChainExceptionHandler, LogoutFilter.class)
                .addFilter(new JwtAuthorizationFilter(authenticationManager, jwtGenerator))
                .authorizeHttpRequests(authorize -> authorize.anyRequest().authenticated())
                .sessionManagement(
                        sessionManager -> sessionManager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .cors(cors ->
                              cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .build();
    }

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
