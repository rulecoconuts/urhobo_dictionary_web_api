package com.fejiro.exploration.dictionary.dictionary_web_api.security.data;

import com.fejiro.exploration.dictionary.dictionary_web_api.database.CRUDDAO;
import com.fejiro.exploration.dictionary.dictionary_web_api.tables.Permission;
import com.fejiro.exploration.dictionary.dictionary_web_api.tables.records.PermissionRecord;
import org.jooq.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.jooq.impl.DSL.row;

@Component
public class PermissionCRUDDAO implements CRUDDAO<PermissionDataObject, Integer> {
    @Autowired
    DSLContext dsl;

    /**
     * Create a permission.
     *
     * @param model Model to be created. {@link PermissionDataObject#id} must be null
     * @return
     */
    @Override
    public PermissionDataObject create(PermissionDataObject model) {
        if (model.getId() != null) throw new IllegalArgumentException("Permission to be created has a non-null id");
        PermissionRecord record = dsl.newRecord(Permission.PERMISSION, model);
        record.store();

        return record.into(PermissionDataObject.class);
    }

    RowN convertRecordToRow(PermissionRecord record, List<Field<?>> fields) {
        List<?> values = fields.stream().map(record::get)
                               .toList();

        return row(values);
    }

    @Override
    public Iterable<PermissionDataObject> createAll(Iterable<PermissionDataObject> models) {
        List<PermissionDataObject> toBeCreatedCollection = StreamSupport.stream(models.spliterator(), false)
                                                                        .toList();
        boolean anyIdsNotNull = toBeCreatedCollection.stream()
                                                     .anyMatch(permissionDataObject -> permissionDataObject.id != null);

        if (anyIdsNotNull) throw new IllegalArgumentException("Permission to be created has a non-null id");
        List<PermissionRecord> recordsToBeCreated = toBeCreatedCollection.stream()
                                                                         .map(permissionDataObject -> dsl.newRecord(
                                                                                 Permission.PERMISSION,
                                                                                 permissionDataObject))
                                                                         .toList();
        var fieldsToInsert = Arrays.stream(Permission.PERMISSION.fields())
                                   .filter(field -> !Optional.of(field.getQualifiedName())
                                                             .map(Name::last)
                                                             .map(name -> name.equals("id"))
                                                             .orElse(false))
                                   .toList();

        List<RowN> valueRows = recordsToBeCreated.stream()
                                                 .map(permissionRecord -> convertRecordToRow(permissionRecord,
                                                                                             fieldsToInsert))
                                                 .toList();

        return dsl.insertInto(Permission.PERMISSION)
                  .columns(fieldsToInsert)
                  .valuesOfRows(valueRows)
                  .returning()
                  .fetchInto(PermissionDataObject.class);
    }

    /**
     * Update a permission.
     *
     * @param model
     * @return
     */
    @Override
    public PermissionDataObject update(PermissionDataObject model) {
        if (model.getId() == null) throw new IllegalArgumentException("Permission to be created has a null id");
        PermissionRecord record = dsl.newRecord(Permission.PERMISSION, model);
        record.store();

        return record.into(PermissionDataObject.class);
    }

    @Override
    public Iterable<PermissionDataObject> updateAll(Iterable<PermissionDataObject> models) {
        List<PermissionRecord> records = StreamSupport.stream(models.spliterator(), false)
                                                      .map(model -> dsl.newRecord(Permission.PERMISSION, model))
                                                      .toList();

        dsl.batchUpdate(records).execute();
        return records.stream().map(permissionRecord -> permissionRecord.into(PermissionDataObject.class))
                      .toList();
    }

    @Override
    public Optional<PermissionDataObject> retrieveById(Integer id) {
        return dsl.select()
                  .from(Permission.PERMISSION)
                  .where(Permission.PERMISSION.ID.eq(id))
                  .fetchOneInto(PermissionDataObject.class);
    }

    @Override
    public Iterable<PermissionDataObject> retrieveAll() {
        return dsl.select().from(Permission.PERMISSION)
                  .fetchInto(PermissionDataObject.class);
    }

    @Override
    public Iterable<PermissionDataObject> retrieveAllById(Iterable<Integer> ids) {
        return dsl.select().from(Permission.PERMISSION)
                  .where(Permission.PERMISSION.ID.in(StreamSupport.stream(ids.spliterator(), false)
                                                                  .collect(Collectors.toUnmodifiableSet())))
                  .fetchInto(PermissionDataObject.class);
    }

    public PermissionDataObject retrieveByName(String name) {
        return dsl.select().from(Permission.PERMISSION)
                  .where(Permission.PERMISSION.NAME.eq(name))
                  .fetchOneInto(PermissionDataObject.class);
    }

    public Iterable<PermissionDataObject> retrieveAllByName(Iterable<String> names) {
        return dsl.select().from(Permission.PERMISSION)
                  .where(Permission.PERMISSION.NAME.in(StreamSupport.stream(names.spliterator(), false)
                                                                    .collect(Collectors.toUnmodifiableSet())))
                  .fetchInto(PermissionDataObject.class);
    }

    @Override
    public void delete(PermissionDataObject model) {
        deleteById(model.getId());
    }

    @Override
    public void deleteById(Integer id) {
        int result = dsl.delete(Permission.PERMISSION)
                        .where(Permission.PERMISSION.ID.eq(id))
                        .execute();
    }

    @Override
    public void deleteAll(Iterable<PermissionDataObject> models) {
        List<Integer> ids = StreamSupport.stream(models.spliterator(), false)
                                         .map(PermissionDataObject::getId)
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
