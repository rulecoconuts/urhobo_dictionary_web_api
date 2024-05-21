package com.fejiro.exploration.dictionary.dictionary_web_api.controller.rest.health;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health-check")
public class HealthCheckController {
    @GetMapping
    ResponseEntity checkHealth() {
        return ResponseEntity.ok().build();
    }
}
