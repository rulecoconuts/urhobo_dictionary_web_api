package com.fejiro.exploration.dictionary.dictionary_web_api.security;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * This bean will load any non-existing permissions into the database
 */
@Component
public class PermissionsDataLoader implements SmartInitializingSingleton {

    @Autowired
    PermissionCRUDDAO permissionCRUDDAO;

    Logger logger = LoggerFactory.getLogger(PermissionsDataLoader.class);

    @Override
    public void afterSingletonsInstantiated() {
        try {

            // Separate the permissions that exist from those that do not
            Set<String> allPossibleNames = Arrays.stream(PermissionName.values())
                                                 .map(Enum::name)
                                                 .collect(Collectors.toUnmodifiableSet());

            Iterable<PermissionDomainObject> existingPermissions = permissionCRUDDAO.retrieveAllByName(allPossibleNames);
            Set<String> existingPermissionNames = StreamSupport.stream(existingPermissions.spliterator(), false)
                                                               .map(PermissionDomainObject::getName)
                                                               .collect(Collectors.toUnmodifiableSet());


            // Create the permissions that do not exist
            Collection<String> nonExistentPermissionNames = CollectionUtils.subtract(allPossibleNames, existingPermissionNames);
            var dialect = permissionCRUDDAO.dsl.dialect();

            if (nonExistentPermissionNames.isEmpty()) {
                logger.info("Skipping permissions data loading: All permissions found");
                return;
            }

            logger.debug("Starting permissions loading");

            Iterable<PermissionDomainObject> newlyCreatedPermissions = permissionCRUDDAO.createAll(nonExistentPermissionNames
                    .stream().map(PermissionDomainObject::new).toList());

            logger.info("Completed permissions loading");
        } catch (Exception e) {
            logger.error(String.format("Failed to load permissions data into database: %s", e.getMessage()), e);
        }
    }
}
