package com.fejiro.exploration.dictionary.dictionary_web_api.config.security;

import com.fejiro.exploration.dictionary.dictionary_web_api.database.CRUDDAO;
import com.fejiro.exploration.dictionary.dictionary_web_api.database.ConfigurableGenericJOOQCRUDDAO;
import com.fejiro.exploration.dictionary.dictionary_web_api.database.GenericJOOQCRUDDAO;
import com.fejiro.exploration.dictionary.dictionary_web_api.database.SimpleGeneralAuditablePopulator;
import com.fejiro.exploration.dictionary.dictionary_web_api.security.PermissionDomainObject;
import com.fejiro.exploration.dictionary.dictionary_web_api.security.RoleDomainObject;
import com.fejiro.exploration.dictionary.dictionary_web_api.tables.Permission;
import com.fejiro.exploration.dictionary.dictionary_web_api.tables.Role;
import com.fejiro.exploration.dictionary.dictionary_web_api.tables.records.PermissionRecord;
import com.fejiro.exploration.dictionary.dictionary_web_api.tables.records.RoleRecord;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Configuration
public class SecurityDataModelConfig {
    @Autowired
    DSLContext dsl;

    @Autowired
    SimpleGeneralAuditablePopulator auditablePopulator;


    @Bean
    CRUDDAO<RoleDomainObject, Integer> getRoleDAO() {
        return ConfigurableGenericJOOQCRUDDAO
                .<RoleDomainObject, Integer, RoleRecord>builder()
                .modelClass(RoleDomainObject.class)
                .dslContext(dsl)
                .table(Role.ROLE)
                .creationPreProcessFunction(roleDomainObject -> {
                    auditablePopulator.populateForCreation(roleDomainObject);
                    return roleDomainObject;
                })
                .updatePreProcessFunction(roleDomainObject -> {
                    auditablePopulator.populateForUpdate(roleDomainObject);
                    return roleDomainObject;
                })
                .idMatchConditionGenerator(Role.ROLE.ID::eq)
                .idCollectionMatchConditionGenerator(
                        ids -> Role.ROLE.ID.in(StreamSupport.stream(ids.spliterator(), false)
                                                            .collect(Collectors.toUnmodifiableSet())))
                .idExtractionFunction(RoleDomainObject::getId)
                .build();
    }

    @Bean
    @Primary
    CRUDDAO<PermissionDomainObject, Integer> getPermissionDAO() {
        return ConfigurableGenericJOOQCRUDDAO
                .<PermissionDomainObject, Integer, PermissionRecord>builder()
                .modelClass(PermissionDomainObject.class)
                .dslContext(dsl)
                .table(Permission.PERMISSION)
                .idMatchConditionGenerator(Permission.PERMISSION.ID::eq)
                .idCollectionMatchConditionGenerator(
                        ids -> Permission.PERMISSION.ID.in(StreamSupport.stream(ids.spliterator(), false)
                                                                        .collect(Collectors.toUnmodifiableSet())))
                .idExtractionFunction(PermissionDomainObject::getId)
                .build();
    }
}
