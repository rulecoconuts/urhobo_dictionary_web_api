package com.fejiro.exploration.dictionary.dictionary_web_api.service.user;

import com.fejiro.exploration.dictionary.dictionary_web_api.database.CRUDDAO;
import com.fejiro.exploration.dictionary.dictionary_web_api.database.ConfigurableGenericJOOQCRUDDAO;
import com.fejiro.exploration.dictionary.dictionary_web_api.database.GenericJOOQCRUDDAO;
import com.fejiro.exploration.dictionary.dictionary_web_api.error_handling.IllegalArgumentExceptionWithMessageMap;
import com.fejiro.exploration.dictionary.dictionary_web_api.tables.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
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
    public AppUserDomainObject create(AppUserDomainObject model) {
        AppUserDataObject dataObject = toData(model);

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

        if (!errors.isEmpty())
            throw new IllegalArgumentExceptionWithMessageMap("Error with creation of app user", errors);

        // Encode/hash password
        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        dataObject.setPassword(encoder.encode(dataObject.getPassword()));

        // Create app user record
        return toDomain(userDAO.create(dataObject));
    }

    AppUserDomainObject toDomain(AppUserDataObject dataObject) {
        return conversionService.convert(dataObject, AppUserDomainObject.class);
    }

    AppUserDataObject toData(AppUserDomainObject domainObject) {
        return conversionService.convert(domainObject, AppUserDataObject.class);
    }

    @Override
    public Iterable<AppUserDomainObject> createAll(Iterable<AppUserDomainObject> models) {
        List<AppUserDataObject> dataObjects = StreamSupport.stream(models.spliterator(), false)
                                                           .map(this::toData)
                                                           .toList();

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

        if (!errorBuilderMap.isEmpty()) {
            var errors = errorBuilderMap.entrySet().stream()
                                        .collect(Collectors.toMap(Map.Entry::getKey,
                                                                  (entry) -> entry.getValue().toString()));
            throw new IllegalArgumentExceptionWithMessageMap("Error with creation of multiple app users", errors);
        }


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

    @Override
    public AppUserDomainObject update(AppUserDomainObject model) {
        AppUserDataObject dataObject = toData(model);

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
        return Optional.empty();
    }

    @Override
    public Optional<AppUserDomainObject> findByEmail(String email) {
        return Optional.empty();
    }
}
