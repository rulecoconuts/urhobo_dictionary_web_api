package com.fejiro.exploration.dictionary.dictionary_web_api.security;

import com.fejiro.exploration.dictionary.dictionary_web_api.database.CRUDDAO;
import com.fejiro.exploration.dictionary.dictionary_web_api.tables.Permission;
import com.fejiro.exploration.dictionary.dictionary_web_api.tables.Word;
import com.fejiro.exploration.dictionary.dictionary_web_api.tables.records.PermissionRecord;
import org.jooq.*;
import org.jooq.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.jooq.impl.DSL.row;

@Component
public class PermissionCRUDDAO implements CRUDDAO<PermissionDomainObject, Integer> {
    @Autowired
    DSLContext dsl;

    /**
     * Create a permission.
     *
     * @param model Model to be created. {@link PermissionDomainObject#id} must be null
     * @return
     */
    @Override
    public PermissionDomainObject create(PermissionDomainObject model) {
        if (model.getId() != null) throw new IllegalArgumentException("Permission to be created has a non-null id");
        PermissionRecord record = dsl.newRecord(Permission.PERMISSION, model);
        record.store();

        return record.into(PermissionDomainObject.class);
    }

    RowN convertRecordToRow(PermissionRecord record, List<Field<?>> fields) {
        List<?> values = fields.stream().map(record::get)
                               .toList();

        return row(values);
    }

    @Override
    public Iterable<PermissionDomainObject> createAll(Iterable<PermissionDomainObject> models) {
        List<PermissionDomainObject> toBeCreatedCollection = StreamSupport.stream(models.spliterator(), false)
                                                                          .toList();
        boolean anyIdsNotNull = toBeCreatedCollection.stream()
                                                     .anyMatch(permissionDomainObject -> permissionDomainObject.id != null);

        if (anyIdsNotNull) throw new IllegalArgumentException("Permission to be created has a non-null id");
        List<PermissionRecord> recordsToBeCreated = toBeCreatedCollection.stream()
                                                                         .map(permissionDomainObject -> dsl.newRecord(Permission.PERMISSION, permissionDomainObject))
                                                                         .toList();
        var fieldsToInsert = Arrays.stream(Permission.PERMISSION.fields())
                                   .filter(field -> !Optional.of(field.getQualifiedName())
                                                             .map(Name::last)
                                                             .map(name -> name.equals("id"))
                                                             .orElse(false))
                                   .toList();

        List<RowN> valueRows = recordsToBeCreated.stream()
                                                 .map(permissionRecord -> convertRecordToRow(permissionRecord, fieldsToInsert))
                                                 .toList();

        return dsl.insertInto(Permission.PERMISSION)
                  .columns(fieldsToInsert)
                  .valuesOfRows(valueRows)
                  .returning()
                  .fetchInto(PermissionDomainObject.class);
    }

    /**
     * Update a permission.
     *
     * @param model
     * @return
     */
    @Override
    public PermissionDomainObject update(PermissionDomainObject model) {
        if (model.getId() == null) throw new IllegalArgumentException("Permission to be created has a null id");
        PermissionRecord record = dsl.newRecord(Permission.PERMISSION, model);
        record.store();

        return record.into(PermissionDomainObject.class);
    }

    @Override
    public Iterable<PermissionDomainObject> updateAll(Iterable<PermissionDomainObject> models) {
        List<PermissionRecord> records = StreamSupport.stream(models.spliterator(), false)
                                                      .map(model -> dsl.newRecord(Permission.PERMISSION, model))
                                                      .toList();

        dsl.batchUpdate(records).execute();
        return records.stream().map(permissionRecord -> permissionRecord.into(PermissionDomainObject.class))
                      .toList();
    }

    @Override
    public PermissionDomainObject retrieveById(Integer id) {
        return dsl.select()
                  .from(Permission.PERMISSION)
                  .where(Permission.PERMISSION.ID.eq(id))
                  .fetchOneInto(PermissionDomainObject.class);
    }

    @Override
    public Iterable<PermissionDomainObject> retrieveAll() {
        return dsl.select().from(Permission.PERMISSION)
                  .fetchInto(PermissionDomainObject.class);
    }

    @Override
    public Iterable<PermissionDomainObject> retrieveAllById(Iterable<Integer> ids) {
        return dsl.select().from(Permission.PERMISSION)
                  .where(Permission.PERMISSION.ID.in(StreamSupport.stream(ids.spliterator(), false)
                                                                  .collect(Collectors.toUnmodifiableSet())))
                  .fetchInto(PermissionDomainObject.class);
    }

    public PermissionDomainObject retrieveByName(String name) {
        return dsl.select().from(Permission.PERMISSION)
                  .where(Permission.PERMISSION.NAME.eq(name))
                  .fetchOneInto(PermissionDomainObject.class);
    }

    public Iterable<PermissionDomainObject> retrieveAllByName(Iterable<String> names) {
        return dsl.select().from(Permission.PERMISSION)
                  .where(Permission.PERMISSION.NAME.in(StreamSupport.stream(names.spliterator(), false)
                                                                    .collect(Collectors.toUnmodifiableSet())))
                  .fetchInto(PermissionDomainObject.class);
    }

    @Override
    public void delete(PermissionDomainObject model) {
        deleteById(model.getId());
    }

    @Override
    public void deleteById(Integer id) {
        int result = dsl.delete(Permission.PERMISSION)
                        .where(Permission.PERMISSION.ID.eq(id))
                        .execute();
    }

    @Override
    public void deleteAll(Iterable<PermissionDomainObject> models) {
        List<Integer> ids = StreamSupport.stream(models.spliterator(), false)
                                         .map(PermissionDomainObject::getId)
                                         .filter(Objects::nonNull)
                                         .toList();
        deleteAllById(ids);
    }

    @Override
    public void deleteAllById(Iterable<Integer> ids) {
        int result = dsl.delete(Permission.PERMISSION)
                        .where(Permission.PERMISSION.ID.in(StreamSupport.stream(ids.spliterator(), false).toList()))
                        .execute();
    }
}
