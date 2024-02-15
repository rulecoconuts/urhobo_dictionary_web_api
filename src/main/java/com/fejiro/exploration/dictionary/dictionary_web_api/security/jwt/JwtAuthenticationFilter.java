package com.fejiro.exploration.dictionary.dictionary_web_api.security.jwt;

import com.fejiro.exploration.dictionary.dictionary_web_api.security.refresh_token.SimpleJwtAndRefreshToken;
import com.fejiro.exploration.dictionary.dictionary_web_api.security.refresh_token.SimpleJwtAndRefresherTokenGenerationService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Authentication filter that reads the credentials(email/username and password) from the HTTP request and returns a JWT
 * for valid credentials.
 */
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    SimpleJwtAndRefresherTokenGenerationService jwtAndRefresherTokenGenerationService;

    public JwtAuthenticationFilter(
            AuthenticationManager authenticationManager,
            SimpleJwtAndRefresherTokenGenerationService jwtAndRefresherTokenGenerationService,
            AuthenticationFailureHandler authenticationFailureHandler) {
        super(authenticationManager);
        setFilterProcessesUrl("/api/login");
        setAuthenticationFailureHandler(authenticationFailureHandler);
        this.jwtAndRefresherTokenGenerationService = jwtAndRefresherTokenGenerationService;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {
        // Make sure request is a POST method
        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException(
                    String.format("Authentication method %s not supported", request.getMethod()));
        }

        String username = obtainUsername(request);
        String email = obtainEmail(request);
        String password = obtainPassword(request);

        String error = validateCredentials(username, email, password);

        if (error != null) {
            throw new AuthenticationServiceException(error);
        }

        UsernamePasswordAuthenticationToken authToken;

        if (username != null) {
            authToken = new UsernamePasswordAuthenticationToken(username.trim(), password);
        } else {
            // Use email if username is not provided
            authToken = new UsernamePasswordAuthenticationToken(email.trim(), password);
        }

        // Set request details using auth token
        setDetails(request, authToken);

        // Authenticate credentials
        return getAuthenticationManager().authenticate(authToken);
    }

    String validateCredentials(String username, String email, String password) {
        Map<String, String> errors = new HashMap<>();
        String usernameOrEmail = Optional.ofNullable(username)
                                         .orElseGet(() -> email);

        if (usernameOrEmail == null) {
            errors.put("usernameOrEmail", "Username or email is required");
        } else if (usernameOrEmail.isBlank()) {
            errors.put("usernameOrEmail", "Username or email cannot be blank");
        }

        if (password == null) {
            errors.put("password", "Password is required");
        } else if (password.isBlank()) {
            errors.put("password", "Password cannot be blank");
        }

        if (errors.isEmpty()) return null;

        return errors.entrySet()
                     .stream()
                     .map(entrySet -> String.format("%s:\"%s\"", entrySet.getKey(), entrySet.getValue()))
                     .collect(Collectors.joining(", "));
    }

    String obtainEmail(HttpServletRequest request) {
        return request.getParameter("email");
    }

    @SneakyThrows
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {
        SimpleJwtAndRefreshToken simpleJwtAndRefreshToken = jwtAndRefresherTokenGenerationService.generate(authResult);
//        super.successfulAuthentication(request, response, chain, authResult);
        response.addHeader(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", simpleJwtAndRefreshToken.getToken()));
        response.addHeader("Refresh-Token", simpleJwtAndRefreshToken.getRefreshToken().getContent());
    }
}
