package com.fejiro.exploration.dictionary.dictionary_web_api.security;

import com.fejiro.exploration.dictionary.dictionary_web_api.database.CRUDDAO;
import com.fejiro.exploration.dictionary.dictionary_web_api.database.GenericJOOQCRUDDAO;
import com.fejiro.exploration.dictionary.dictionary_web_api.security.data.PermissionDataObject;
import com.fejiro.exploration.dictionary.dictionary_web_api.tables.Permission;
import com.fejiro.exploration.dictionary.dictionary_web_api.tables.records.PermissionRecord;
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
    CRUDDAO<PermissionDataObject, Integer> permissionCRUDDAO;

    Logger logger = LoggerFactory.getLogger(PermissionsDataLoader.class);

    @Override
    public void afterSingletonsInstantiated() {
        try {
            logger.info("About to attempt loading permissions data");
            // Separate the permissions that exist from those that do not
            Set<String> allPossibleNames = Arrays.stream(PermissionName.values())
                                                 .map(Enum::name)
                                                 .collect(Collectors.toUnmodifiableSet());

            Iterable<PermissionDataObject> existingPermissions = ((GenericJOOQCRUDDAO<PermissionDataObject, Integer, PermissionRecord>) permissionCRUDDAO).retrieveAll(
                    Permission.PERMISSION.NAME.in(allPossibleNames)
            );
            Set<String> existingPermissionNames = StreamSupport.stream(existingPermissions.spliterator(), false)
                                                               .map(PermissionDataObject::getName)
                                                               .collect(Collectors.toUnmodifiableSet());


            // Create the permissions that do not exist
            Collection<String> nonExistentPermissionNames = CollectionUtils.subtract(allPossibleNames,
                                                                                     existingPermissionNames);

            if (nonExistentPermissionNames.isEmpty()) {
                logger.info("Skipping permissions data loading: All permissions found");
                return;
            }

            logger.debug("Starting permissions loading");

            Iterable<PermissionDataObject> newlyCreatedPermissions = permissionCRUDDAO.createAll(
                    nonExistentPermissionNames
                            .stream().map(PermissionDataObject::new).toList());

            logger.info("Completed permissions loading");
        } catch (Exception e) {
            logger.error(String.format("Failed to load permissions data into database: %s", e.getMessage()), e);
        }
    }
}
