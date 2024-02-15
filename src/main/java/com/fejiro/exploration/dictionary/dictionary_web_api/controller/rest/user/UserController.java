package com.fejiro.exploration.dictionary.dictionary_web_api.controller.rest.user;

import com.fejiro.exploration.dictionary.dictionary_web_api.error_handling.IllegalArgumentExceptionWithMessageMap;
import com.fejiro.exploration.dictionary.dictionary_web_api.security.CustomSecurityUserDetails;
import com.fejiro.exploration.dictionary.dictionary_web_api.security.refresh_token.SimpleJwtAndRefreshToken;
import com.fejiro.exploration.dictionary.dictionary_web_api.security.refresh_token.SimpleJwtAndRefresherTokenGenerationService;
import com.fejiro.exploration.dictionary.dictionary_web_api.service.user.AppUserDomainObject;
import com.fejiro.exploration.dictionary.dictionary_web_api.service.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    UserService userService;

    Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    SimpleJwtAndRefresherTokenGenerationService jwtAndRefresherTokenGenerationService;

    @PostMapping("/register")
    ResponseEntity register(@RequestBody AppUserDomainObject user) throws IllegalArgumentExceptionWithMessageMap {
        logger.info("About to register new user");
        var createdUser = userService.create(user);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/refresh")
    ResponseEntity refreshToken(HttpServletRequest request,
                                HttpServletResponse response) throws IllegalArgumentExceptionWithMessageMap {
        String token = request.getHeader(HttpHeaders.AUTHORIZATION)
                              .replaceFirst("Bearer ", "");
        String refreshToken = request.getHeader("Refresh-Token");

        SimpleJwtAndRefreshToken simpleJwtAndRefreshToken = jwtAndRefresherTokenGenerationService.refresh(token,
                                                                                                          refreshToken);

        response.addHeader(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", simpleJwtAndRefreshToken.getToken()));
        response.addHeader("Refresh-Token", simpleJwtAndRefreshToken.getRefreshToken().getContent());

        return ResponseEntity.ok().build();
    }

    @GetMapping("/self")
    ResponseEntity getLoggedInUserDetails(Authentication authentication) {
        var userCopy = ((AppUserDomainObject) authentication.getPrincipal())
                .toBuilder().build();
        userCopy.setPassword(null);


        return ResponseEntity.ok(userCopy);
    }
}
