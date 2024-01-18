package com.fejiro.exploration.dictionary.dictionary_web_api.config.security;

import com.fejiro.exploration.dictionary.dictionary_web_api.database.CRUDDAO;
import com.fejiro.exploration.dictionary.dictionary_web_api.database.ConfigurableGenericJOOQCRUDDAO;
import com.fejiro.exploration.dictionary.dictionary_web_api.database.SimpleGeneralAuditablePopulator;
import com.fejiro.exploration.dictionary.dictionary_web_api.security.data.PermissionDataObject;
import com.fejiro.exploration.dictionary.dictionary_web_api.security.data.RoleDataObject;
import com.fejiro.exploration.dictionary.dictionary_web_api.security.data.RolePermissionDataObject;
import com.fejiro.exploration.dictionary.dictionary_web_api.security.data.RolePermissionDataObjectId;
import com.fejiro.exploration.dictionary.dictionary_web_api.tables.Permission;
import com.fejiro.exploration.dictionary.dictionary_web_api.tables.Role;
import com.fejiro.exploration.dictionary.dictionary_web_api.tables.RolePermission;
import com.fejiro.exploration.dictionary.dictionary_web_api.tables.records.PermissionRecord;
import com.fejiro.exploration.dictionary.dictionary_web_api.tables.records.RolePermissionRecord;
import com.fejiro.exploration.dictionary.dictionary_web_api.tables.records.RoleRecord;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Configuration
public class SecurityDataModelConfig {
    @Autowired
    DSLContext dsl;

    @Autowired
    SimpleGeneralAuditablePopulator auditablePopulator;

    @Bean
    CRUDDAO<RoleDataObject, Integer> getRoleDAO() {
        return ConfigurableGenericJOOQCRUDDAO
                .<RoleDataObject, Integer, RoleRecord>builder()
                .modelClass(RoleDataObject.class)
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
                .idExtractionFunction(RoleDataObject::getId)
                .build();
    }

    @Bean
    @Primary
    CRUDDAO<PermissionDataObject, Integer> getPermissionDAO() {
        return ConfigurableGenericJOOQCRUDDAO
                .<PermissionDataObject, Integer, PermissionRecord>builder()
                .modelClass(PermissionDataObject.class)
                .dslContext(dsl)
                .table(Permission.PERMISSION)
                .idMatchConditionGenerator(Permission.PERMISSION.ID::eq)
                .idCollectionMatchConditionGenerator(
                        ids -> Permission.PERMISSION.ID.in(StreamSupport.stream(ids.spliterator(), false)
                                                                        .collect(Collectors.toUnmodifiableSet())))
                .idExtractionFunction(PermissionDataObject::getId)
                .build();
    }

    @Bean
    CRUDDAO<RolePermissionDataObject, RolePermissionDataObjectId> getRolePermission() {
        final Function<RolePermissionDataObjectId, Condition> idMatchGenerator = id -> {
            return RolePermission.ROLE_PERMISSION.PERMISSION_ID.eq(id.getPermissionId())
                                                               .and(RolePermission.ROLE_PERMISSION.ROLE_ID.eq(
                                                                       id.getRoleId()));
        };
        return ConfigurableGenericJOOQCRUDDAO
                .<RolePermissionDataObject, RolePermissionDataObjectId, RolePermissionRecord>builder().
                modelClass(RolePermissionDataObject.class)
                .dslContext(dsl)
                .table(RolePermission.ROLE_PERMISSION)
                .idMatchConditionGenerator(idMatchGenerator)
                .idCollectionMatchConditionGenerator(ids -> {
                    return StreamSupport.stream(ids.spliterator(), false)
                                        .map(idMatchGenerator)
                                        .collect(DSL::noCondition
                                                , Condition::or,
                                                 Condition::or);
                })
                .idExtractionFunction(RolePermissionDataObject::getId)
                .creationPreProcessFunction(rolePermissionDataObject -> {
                    auditablePopulator.populateForCreation(rolePermissionDataObject);
                    return rolePermissionDataObject;
                })
                .updatePreProcessFunction(rolePermissionDataObject -> {
                    auditablePopulator.populateForUpdate(rolePermissionDataObject);
                    return rolePermissionDataObject;
                })
                .build();
    }
}
