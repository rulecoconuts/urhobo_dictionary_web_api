package com.fejiro.exploration.dictionary.dictionary_web_api.security;

import com.fejiro.exploration.dictionary.dictionary_web_api.error_handling.ApiError;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import io.jsonwebtoken.MalformedJwtException;

@Component
public class FilterChainExceptionHandler extends OncePerRequestFilter {
    @Autowired
    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver resolver;

    @Autowired
    Jackson2ObjectMapperBuilder mapperBuilder;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (MalformedJwtException malformedJwtException) {
            handleDefaultException(request, response, malformedJwtException, HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            handleDefaultException(request, response, e, null);
        }
    }

    void handleDefaultException(HttpServletRequest request, HttpServletResponse response,
                                Exception exception, HttpStatus status) throws IOException {
        ApiError apiError = ApiError.builder()
                                    .message(exception.getMessage())
                                    .status(Optional.ofNullable(status).orElse(HttpStatus.INTERNAL_SERVER_ERROR))
                                    .timestamp(OffsetDateTime.now())
                                    .build();

        applyResponseEntityToServletResponse(convertApiErrorToResponseEntity(apiError), response);
    }

    /**
     * Populate response headers, status code and body according to responseEntity
     *
     * @param responseEntity
     * @param response
     * @throws IOException
     */
    void applyResponseEntityToServletResponse(ResponseEntity responseEntity,
                                              HttpServletResponse response) throws IOException {
        for (Map.Entry<String, List<String>> header : responseEntity.getHeaders().entrySet()) {
            String chave = header.getKey();
            for (String valor : header.getValue()) {
                response.addHeader(chave, valor);
            }
        }

        response.setStatus(responseEntity.getStatusCodeValue());

        if (responseEntity.getBody() instanceof String) {
            response.getWriter().write((String) responseEntity.getBody());
        } else if (responseEntity.getBody() != null) {
            // Convert body to json before writing it to response
            String json = mapperBuilder.build().writeValueAsString(responseEntity.getBody());
            response.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");
            response.getWriter().write(json);
        }
    }

    ResponseEntity<Object> convertApiErrorToResponseEntity(ApiError error) {
        return ResponseEntity.status(error.getStatus())
                             .body(error);
    }
}
