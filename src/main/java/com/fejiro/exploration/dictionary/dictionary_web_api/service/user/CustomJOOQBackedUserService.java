package com.fejiro.exploration.dictionary.dictionary_web_api.service.user;

import com.fejiro.exploration.dictionary.dictionary_web_api.database.CRUDDAO;
import com.fejiro.exploration.dictionary.dictionary_web_api.database.GenericJOOQCRUDDAO;
import com.fejiro.exploration.dictionary.dictionary_web_api.error_handling.IllegalArgumentExceptionWithMessageMap;
import com.fejiro.exploration.dictionary.dictionary_web_api.tables.AppUser;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class CustomJOOQBackedUserService implements UserService {
    @Autowired
    CRUDDAO<AppUserDataObject, Integer> userDAO;

    @Autowired
    ConversionService conversionService;


    @Override
    public AppUserDomainObject create(AppUserDomainObject model) throws IllegalArgumentExceptionWithMessageMap {
        AppUserDataObject dataObject = toData(model);

        throwIfDataModelIsInvalidForCreation(dataObject);

        // Encode/hash password
        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        dataObject.setPassword(encoder.encode(dataObject.getPassword()));

        // Create app user record
        return toDomain(userDAO.create(dataObject));
    }

    Map<String, String> validateModelForCreation(AppUserDomainObject model) {
        return validateDataModelForCreation(toData(model));
    }

    /**
     * Check if user data is valid to be created
     *
     * @param dataObject
     * @return
     */
    Map<String, String> validateDataModelForCreation(AppUserDataObject dataObject) {
        // Make sure email and username are unique
        Iterable<AppUserDataObject> matches = ((GenericJOOQCRUDDAO<AppUserDataObject, Integer, ?>) userDAO).retrieveAll(
                AppUser.APP_USER.EMAIL.eq(dataObject.getEmail())
                                      .or(AppUser.APP_USER.USERNAME.eq(dataObject.getUsername()))
        );

        Map<String, String> errors = new HashMap<>();

        matches.forEach((match) -> {
            if (match.getEmail().equals(dataObject.getEmail()))
                errors.put("email", String.format("Email %s already exists", dataObject.getEmail()));
            if (match.getUsername().equals(dataObject.getUsername()))
                errors.put("username", String.format("Username %s already exists", dataObject.getUsername()));
        });

        if (dataObject.getPassword() == null || dataObject.getPassword().trim().isEmpty()) {
            errors.put("password", "Password cannot be blank");
        }

        if (dataObject.getFirstName() == null || dataObject.getFirstName().trim().isEmpty()) {
            errors.put("firstName", "First name cannot be blank");
        }

        if (dataObject.getUsername() == null || dataObject.getUsername().trim().isEmpty()) {
            errors.put("username", "Username cannot be blank");
        } else if (dataObject.getUsername().contains("@")) {
            errors.put("username", "Username cannot contain '@'");
        }

        if (dataObject.getEmail() == null || dataObject.getEmail().trim().isEmpty()) {
            errors.put("email", "Email cannot be blank");
        } else if (!EmailValidator.getInstance().isValid(dataObject.getEmail())) {
            errors.put("email", "Invalid email format");
        }

        return errors;
    }

    /**
     * Throw an error if user data is not valid to be created
     *
     * @param dataObject
     * @throws IllegalArgumentExceptionWithMessageMap
     */
    void throwIfDataModelIsInvalidForCreation(
            AppUserDataObject dataObject) throws IllegalArgumentExceptionWithMessageMap {
        Map<String, String> errors = new HashMap<>();

        if (!errors.isEmpty())
            throw new IllegalArgumentExceptionWithMessageMap("Error with creation of app user", errors);
    }

    /**
     * Throw an exception if domain object model is invalid for creation
     *
     * @param model
     * @throws IllegalArgumentExceptionWithMessageMap
     */
    void throwIfModelIsInvalidForCreation(AppUserDomainObject model) throws IllegalArgumentExceptionWithMessageMap {
        throwIfDataModelIsInvalidForCreation(toData(model));
    }

    AppUserDomainObject toDomain(AppUserDataObject dataObject) {
        return conversionService.convert(dataObject, AppUserDomainObject.class);
    }

    AppUserDataObject toData(AppUserDomainObject domainObject) {
        return conversionService.convert(domainObject, AppUserDataObject.class);
    }

    @Override
    public Iterable<AppUserDomainObject> createAll(
            Iterable<AppUserDomainObject> models) throws IllegalArgumentExceptionWithMessageMap {
        List<AppUserDataObject> dataObjects = StreamSupport.stream(models.spliterator(), false)
                                                           .map(this::toData)
                                                           .toList();

        throwIfDataModelsAreInvalidForCreation(dataObjects);

        // Encode/hash all the passwords
        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

        dataObjects.forEach((dataObject) ->
                                    dataObject.setPassword(encoder.encode(dataObject.getPassword()))
        );


        // Create app user records
        return StreamSupport.stream(userDAO.createAll(dataObjects)
                                           .spliterator(), false)
                            .map(this::toDomain)
                            .collect(Collectors.toSet());
    }

    /**
     * Check if collection of user domain data is valid before creation
     *
     * @param models
     * @return
     */
    Map<String, String> validateModelsForCreation(Collection<AppUserDomainObject> models) {
        List<AppUserDataObject> dataObjects = StreamSupport.stream(models.spliterator(), false)
                                                           .map(this::toData)
                                                           .toList();
        return validateDataModelsForCreation(dataObjects);
    }

    /**
     * Check if collection of user data is valid before creation
     * TODO: Make errors returned show the invalid user data objects more clearly
     *
     * @param dataObjects
     * @return
     */
    Map<String, String> validateDataModelsForCreation(Collection<AppUserDataObject> dataObjects) {

        // Make sure email and username are unique for all models
        Set<String> emailsToBeAdded = dataObjects.stream().map(AppUserDataObject::getEmail)
                                                 .collect(Collectors.toSet());
        Set<String> usernamesToBeAdded = dataObjects.stream().map(AppUserDataObject::getUsername)
                                                    .collect(Collectors.toSet());


        Iterable<AppUserDataObject> matches = ((GenericJOOQCRUDDAO<AppUserDataObject, Integer, ?>) userDAO).retrieveAll(
                AppUser.APP_USER.EMAIL.in(emailsToBeAdded)
                                      .or(AppUser.APP_USER.USERNAME.in(usernamesToBeAdded))
        );

        Map<String, StringBuilder> errorBuilderMap = new HashMap<>();

        matches.forEach((match) -> {
            if (emailsToBeAdded.contains(match.getEmail())) {
                errorBuilderMap.putIfAbsent("email", new StringBuilder());
                StringBuilder builder =
                        errorBuilderMap.get("email");
                if (!builder.isEmpty()) {
                    builder.append(";");
                }
                builder.append(String.format("Email %s already exists", match.getEmail()));
            }

            if (usernamesToBeAdded.contains(match.getUsername())) {
                errorBuilderMap.putIfAbsent("username", new StringBuilder());
                StringBuilder builder =
                        errorBuilderMap.get("username");
                if (!builder.isEmpty()) {
                    builder.append(";");
                }
                builder.append(String.format("Username %s already exists", match.getUsername()));
            }
        });

        return errorBuilderMap.entrySet().stream()
                              .collect(Collectors.toMap(Map.Entry::getKey,
                                                        (entry) -> entry.getValue().toString()));
    }

    /**
     * Throw an exception if collection of user domain data is invalid for creation
     *
     * @param dataObjects
     * @throws IllegalArgumentExceptionWithMessageMap
     */
    void throwIfDataModelsAreInvalidForCreation(
            Collection<AppUserDataObject> dataObjects) throws IllegalArgumentExceptionWithMessageMap {
        var errors = validateDataModelsForCreation(dataObjects);

        if (!errors.isEmpty()) {

            throw new IllegalArgumentExceptionWithMessageMap("Error with creation of multiple app users", errors);
        }
    }

    @Override
    public AppUserDomainObject update(AppUserDomainObject model) {
        AppUserDataObject dataObject = toData(model);
        // TODO: Add validation code

        return toDomain(userDAO.update(dataObject));
    }

    @Override
    public Iterable<AppUserDomainObject> updateAll(Iterable<AppUserDomainObject> models) {
        List<AppUserDataObject> dataObjects = StreamSupport.stream(models.spliterator(), false)
                                                           .map(this::toData)
                                                           .toList();

        return StreamSupport.stream(userDAO.updateAll(dataObjects)
                                           .spliterator(), false)
                            .map(this::toDomain)
                            .collect(Collectors.toSet());
    }

    @Override
    public Optional<AppUserDomainObject> retrieveById(Integer id) {
        return Optional.empty();
    }

    @Override
    public Iterable<AppUserDomainObject> retrieveAll() {
        return null;
    }

    @Override
    public Iterable<AppUserDomainObject> retrieveAllById(Iterable<Integer> ids) {
        return null;
    }

    @Override
    public void delete(AppUserDomainObject model) {

    }

    @Override
    public void deleteById(Integer id) {

    }

    @Override
    public void deleteAll(Iterable<AppUserDomainObject> models) {

    }

    @Override
    public void deleteAllById(Iterable<Integer> ids) {

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
}
