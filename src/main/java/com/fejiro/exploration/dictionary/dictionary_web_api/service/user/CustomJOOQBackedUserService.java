package com.fejiro.exploration.dictionary.dictionary_web_api.service.user;

import com.fejiro.exploration.dictionary.dictionary_web_api.database.CRUDDAO;
import com.fejiro.exploration.dictionary.dictionary_web_api.database.GenericJOOQCRUDDAO;
import com.fejiro.exploration.dictionary.dictionary_web_api.error_handling.IllegalArgumentExceptionWithMessageMap;
import com.fejiro.exploration.dictionary.dictionary_web_api.service.GenericJOOQBackedService;
import com.fejiro.exploration.dictionary.dictionary_web_api.tables.AppUser;
import org.apache.commons.validator.routines.EmailValidator;
import org.jooq.Condition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class CustomJOOQBackedUserService implements UserService, GenericJOOQBackedService<AppUserDomainObject, AppUserDataObject, Integer> {
    @Autowired
    CRUDDAO<AppUserDataObject, Integer> userDAO;

    @Autowired
    ConversionService conversionService;


    @Override
    public CRUDDAO<AppUserDataObject, Integer> getCRUDAO() {
        return userDAO;
    }

    @Override
    public AppUserDomainObject preProcessBeforeCreation(AppUserDomainObject model) {
        // Encode/hash password
        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        model.setPassword(encoder.encode(model.getPassword()));
        return model;
    }

    @Override
    public Map<String, String> validateModelForCreation(AppUserDomainObject model) {
        // Make sure email and username are unique
        Condition condition = AppUser.APP_USER.EMAIL.eq(model.getEmail());
        if (model.getUsername() != null) {
            condition = condition.or(AppUser.APP_USER.USERNAME.eq(model.getUsername()));
        }

        Iterable<AppUserDataObject> matches = ((GenericJOOQCRUDDAO<AppUserDataObject, Integer, ?>) userDAO).retrieveAll(
                condition
        );

        Map<String, String> errors = new HashMap<>();

        matches.forEach((match) -> {
            if (match.getEmail().equals(model.getEmail()))
                errors.put("email", String.format("Email %s already exists", model.getEmail()));
            if (model.getUsername() != null && match.getUsername().equals(model.getUsername()))
                errors.put("username", String.format("Username %s already exists", model.getUsername()));
        });

        performSharedValidation(errors, model.getPassword(), model.getFirstName(), model.getUsername(),
                                model.getEmail(), model);

        return errors;
    }

    @Override
    public String generateErrorLabel(AppUserDomainObject model) {
        return String.format("%s_%s", model.getId(), model.getEmail());
    }

    @Override
    public Integer getId(AppUserDomainObject model) {
        return model.getId();
    }

    @Override
    public Map<String, String> validateModelForUpdate(AppUserDomainObject model,
                                                      Optional<AppUserDomainObject> existingCopy) {
        Map<String, String> errors = new HashMap<>();

        performSharedValidation(errors, model.getPassword(), model.getFirstName(), model.getUsername(),
                                model.getEmail(), model);


        return errors;
    }

    /**
     * Perform validation that is shared across creation and update of app_users
     *
     * @param errors
     * @param password
     * @param firstName
     * @param username
     * @param email
     * @param model
     */
    private void performSharedValidation(Map<String, String> errors, String password, String firstName, String username,
                                         String email, AppUserDomainObject model) {
        if (password == null || password.trim().isEmpty()) {
            errors.put("password", "Password cannot be blank");
        }

        if (firstName == null || firstName.trim().isEmpty()) {
            errors.put("firstName", "First name cannot be blank");
        }

        if (username != null && username.trim().isEmpty()) {
            errors.put("username", "Username cannot be blank");
        } else if (username != null && username.contains("@")) {
            errors.put("username", "Username cannot contain '@'");
        }

        if (email == null || email.trim().isEmpty()) {
            errors.put("email", "Email cannot be blank");
        } else if (!EmailValidator.getInstance().isValid(email)) {
            errors.put("email", "Invalid email format");
        }
    }

    @Override
    public Optional<AppUserDomainObject> findByUsername(String username) {
        return ((GenericJOOQCRUDDAO<AppUserDataObject, Integer, ?>) userDAO)
                .retrieveOne(AppUser.APP_USER.USERNAME.eq(username))
                .map(this::toDomain);
    }

    @Override
    public Optional<AppUserDomainObject> findByEmail(String email) {
        return ((GenericJOOQCRUDDAO<AppUserDataObject, Integer, ?>) userDAO)
                .retrieveOne(AppUser.APP_USER.EMAIL.eq(email))
                .map(this::toDomain);
    }

    @Override
    public ConversionService getConversionService() {
        return conversionService;
    }

    @Override
    public Class<? extends AppUserDomainObject> getDomainClass() {
        return AppUserDomainObject.class;
    }

    @Override
    public Class<? extends AppUserDataObject> getDataClass() {
        return AppUserDataObject.class;
    }
}
