package com.fejiro.exploration.dictionary.dictionary_web_api.security;

import com.fejiro.exploration.dictionary.dictionary_web_api.error_handling.ApiError;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.handler.HandlerExceptionResolverComposite;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

@Component
public class RESTExceptionHandlerBackedAuthenticationFailureHandler implements AuthenticationFailureHandler {
    @Autowired
    Jackson2ObjectMapperBuilder mapperBuilder;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {

        var responseEntity = handleAuthenticationException(exception);


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

    protected ResponseEntity<Object> handleAuthenticationException(AuthenticationException authenticationException) {
        ApiError error = ApiError.builder()
                                 .status(HttpStatus.FORBIDDEN)
                                 .message(authenticationException.getMessage())
                                 .timestamp(OffsetDateTime.now())
                                 .build();

        return convertApiErrorToResponseEntity(error);
    }


    ResponseEntity<Object> convertApiErrorToResponseEntity(ApiError error) {
        return ResponseEntity.status(error.getStatus())
                             .body(error);
    }
}
