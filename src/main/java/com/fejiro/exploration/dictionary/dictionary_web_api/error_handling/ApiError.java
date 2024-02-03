package com.fejiro.exploration.dictionary.dictionary_web_api.error_handling;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

@Data
@Builder
public class ApiError {
    private HttpStatus status;
    //    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private OffsetDateTime timestamp;
    private String message;
    private String debugMessage;

    @Singular
    private Map<String, String> errorMessages;
}
