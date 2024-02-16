package com.fejiro.exploration.dictionary.dictionary_web_api.database;

import org.jooq.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.jooq.impl.DSL.row;

/**
 * Generate a JOOQ-based Create-Retrieve-Update-Delete data access object
 * Author: Oghenefejiro Abohweyere
 *
 * @param <T> Type of the data object model that will be used to interact with this interface
 * @param <I> Type of the id from the data object model [T]
 * @param <R> The updatable record to be used for JOOQ operations.
 */
public interface GenericJOOQCRUDDAO<T, I, R extends UpdatableRecord<R>> extends CRUDDAO<T, I> {
    DSLContext getDsl();

    /**
     * Get the class of the data object model
     *
     * @return
     */
    Class<T> getModelClass();

    /**
     * Validate whether a model is ready for creation
     *
     * @param model
     * @return Non-null if there is an error
     */
    default String validateForCreation(T model) {
        if (getId(model) != null) return "Model to be created has a non-null id";

        return null;
    }

    /**
     * Validate whether an iterable of models is ready for creation
     *
     * @param models
     * @return Non-null if there is an error
     */
    default String validateForCreation(Iterable<T> models) {
        List<T> toBeCreatedCollection = StreamSupport.stream(models.spliterator(), false)
                                                     .toList();
        return validateForCreation(toBeCreatedCollection);
    }

    /**
     * Validate whether a collection of models is ready for creation
     *
     * @param models
     * @return Non-null if there is an error
     */
    default String validateForCreation(Collection<T> models) {
        if (models.isEmpty()) return "Empty model collection. Nothing to create";

        String compositeError = models.stream().map(this::validateForCreation)
                                      .filter(Objects::nonNull)
                                      .collect(Collectors.joining(",\n"));

        if (compositeError.isEmpty()) return null;

        return compositeError;
    }

    /**
     * Perform any necessary mutations on a model before creation.
     * It is recommended for this method to return a completely new instance of [T].
     * The default method of this method just returns [model].
     *
     * @param model
     * @return new instance of [T] that is ready for creation
     */
    default T preProcessDataForCreation(T model) {
        return model;
    }

    /**
     * Get the table that represents the data object model [T] in the database
     *
     * @return
     */
    Table<R> getTable();

    default R generateUpdatableRecord(T model) {
        return getDsl().newRecord(getTable(), model);
    }

    /**
     * Create model
     *
     * @param model Model to be created
     * @return
     */
    @Override
    public default T create(T model) {
        // Validate model for creation
        String error = validateForCreation(model);
        if (error != null) throw new IllegalArgumentException(error);

        // Add additional data to model if necessary
        T preProcessedModel = preProcessDataForCreation(model);

        // Create record
        R record = generateUpdatableRecord(preProcessedModel);

        record.store();

        return record.into((Class<? extends T>) model.getClass());
    }

    /**
     * Get fields that should be included in creation
     *
     * @return
     */
    default List<Field<?>> getFieldsForCreation() {
        return Arrays.stream(getTable().fields())
                     .filter(field -> !Optional.of(field.getQualifiedName())
                                               .map(Name::last)
                                               .map(name -> name.equals("id"))
                                               .orElse(false))
                     .toList();
    }

    default List<Field<?>> getFieldsWithName(List<String> names) {
        return Arrays.stream(getTable().fields())
                     .filter(field -> !Optional.of(field.getQualifiedName())
                                               .map(Name::last)
                                               .map(names::contains)
                                               .orElse(false))
                     .sorted((a, b) -> {
                         Integer aIndex = names.indexOf(a.getQualifiedName().last());
                         Integer bIndex = names.indexOf(b.getQualifiedName().last());

                         return aIndex.compareTo(bIndex);
                     })
                     .toList();
    }

    default Optional<Field<?>> getFieldWithName(String name) {
        return getFieldsWithName(List.of(name)).stream().findFirst();
    }

    /**
     * Convert a record to a row; only including values of the provided fields
     *
     * @param record
     * @param fields
     * @return
     */
    default RowN convertRecordToRow(R record, List<Field<?>> fields) {
        List<?> values = fields.stream().map(record::get)
                               .toList();

        return row(values);
    }

    /**
     * Create multiple models in one network request
     *
     * @param models
     * @return
     */
    @Override
    public default Iterable<T> createAll(Iterable<T> models) {
        List<T> toBeCreatedCollection = StreamSupport.stream(models.spliterator(), false)
                                                     .toList();
        // validate models for creation
        String error = validateForCreation(toBeCreatedCollection);

        if (error != null) throw new IllegalArgumentException(error);

        // Generate records
        List<R> recordsToBeCreated = toBeCreatedCollection.stream()
                                                          .map(this::preProcessDataForCreation)
                                                          .map(this::generateUpdatableRecord)
                                                          .toList();

        var fieldsToInsert = getFieldsForCreation();

        List<RowN> valueRows = recordsToBeCreated.stream()
                                                 .map(record -> convertRecordToRow(record, fieldsToInsert))
                                                 .toList();


        return getDsl().insertInto(getTable())
                       .columns(fieldsToInsert)
                       .valuesOfRows(valueRows)
                       .returning()
                       .fetchInto((Class<? extends T>)
                                          toBeCreatedCollection.get(0).getClass()
                       );
    }

    /**
     * Validate whether a model is ready for update
     *
     * @param model
     * @return Non-null if there is an error
     */
    default String validateForUpdate(T model) {
        if (getId(model) == null) return "Model to be updated has null id";

        return null;
    }

    /**
     * Validate whether an iterable of models is ready for update
     *
     * @param models
     * @return Non-null if there is an error
     */
    default String validateForUpdate(Iterable<T> models) {
        List<T> toBeCreatedCollection = StreamSupport.stream(models.spliterator(), false)
                                                     .toList();
        return validateForUpdate(toBeCreatedCollection);
    }


    /**
     * Validate whether a collection of models is ready for update
     *
     * @param models
     * @return Non-null if there is an error
     */
    default String validateForUpdate(Collection<T> models) {
        if (models.isEmpty()) return "Empty model collection. Nothing to update";

        String compositeError = models.stream().map(this::validateForUpdate)
                                      .filter(Objects::nonNull)
                                      .collect(Collectors.joining(",\n"));

        if (compositeError.isEmpty()) return null;

        return compositeError;
    }

    /**
     * Perform any necessary mutations on a model before update.
     * It is recommended for this method to return a completely new instance of [T].
     * The default method of this method just returns [model].
     *
     * @param model
     * @return new instance of [T] that is ready for update
     */
    default T preProcessDataForUpdate(T model) {
        return model;
    }

    /**
     * Update model
     *
     * @param model
     * @return
     */
    @Override
    default T update(T model) {
        String error = validateForUpdate(model);
        if (error != null) throw new IllegalArgumentException(error);
        T preProcessedModel = preProcessDataForUpdate(model);
        R record = generateUpdatableRecord(preProcessedModel);

        record.store();

        return record.into((Class<? extends T>) model.getClass());
    }

    /**
     * Update multiple models
     *
     * @param models
     * @return
     */
    @Override
    default Iterable<T> updateAll(Iterable<T> models) {
        List<T> modelsToBeUpdated = StreamSupport.stream(models.spliterator(), false)
                                                 .toList();
        String error = validateForUpdate(modelsToBeUpdated);

        if (error != null) throw new IllegalArgumentException(error);

        List<R> records = modelsToBeUpdated.stream()
                                           .map(this::preProcessDataForUpdate)
                                           .map(this::generateUpdatableRecord)
                                           .toList();

        getDsl().batchUpdate(records).execute();

        Class<? extends T> modelType = (Class<? extends T>) modelsToBeUpdated.get(0).getClass();
        return records.stream().map(permissionRecord -> (T) permissionRecord.into(modelType))
                      .toList();
    }

    /**
     * Get condition for finding models that have the supplied id
     *
     * @param id
     * @return
     */
    Condition getIdEqCondition(I id);

    /**
     * Get condition for finding models that have an id in [ids]
     *
     * @param ids
     * @return
     */
    Condition getIdEqCondition(Iterable<I> ids);

    /**
     * Retrieve model linked to [id]
     *
     * @param id
     * @return
     */
    @Override
    public default Optional<T> retrieveById(I id) {
        return Optional.ofNullable(getDsl().select()
                                           .from(getTable())
                                           .where(getIdEqCondition(id))
                                           .fetchOneInto(getModelClass()));
    }

    default Optional<T> retrieveOne(Condition condition) {
        return Optional.ofNullable(getDsl().select()
                                           .from(getTable())
                                           .where(condition)
                                           .fetchOneInto(getModelClass()));
    }


    default Optional<T> retrieveOne(Condition condition, Collection<? extends OrderField<?>> orderFields) {
        return Optional.ofNullable(getDsl().select()
                                           .from(getTable())
                                           .where(condition)
                                           .orderBy(orderFields)
                                           .fetchOneInto(getModelClass()));
    }

    default Optional<T> retrieveOne(Condition condition, Pageable pageable) {
        return Optional.ofNullable(getDsl().select()
                                           .from(getTable())
                                           .where(condition)
                                           .orderBy(getSortFields(pageable.getSort()))
                                           .fetchOneInto(getModelClass()));
    }

    default Iterable<T> retrieveAll(Condition condition) {
        return getDsl().select()
                       .from(getTable())
                       .where(condition)
                       .fetchInto(getModelClass());
    }

    default Iterable<T> retrieveAll(Condition condition, Collection<? extends OrderField<?>> orderFields) {
        return getDsl().select()
                       .from(getTable())
                       .where(condition)
                       .orderBy(orderFields)
                       .fetchInto(getModelClass());
    }

    default Page<T> retrieveAll(Condition condition, Pageable pageable) {
        List<T> results = getDsl().select()
                                  .from(getTable())
                                  .where(condition)
                                  .orderBy(getSortFields(pageable.getSort()))
                                  .limit(pageable.getPageSize())
                                  .offset(pageable.getOffset())
                                  .fetchInto(getModelClass());
        return new PageImpl<>(results, pageable, count(condition));
    }

    default long count(Condition condition) {
        return getDsl()
                .selectCount()
                .from(getTable())
                .where(condition)
                .fetchOne(0, long.class);
    }

    default long count() {
        return getDsl()
                .selectCount()
                .from(getTable())
                .fetchOne(0, long.class);
    }

    private Collection<SortField<?>> getSortFields(Sort sortSpecification) {
        return sortSpecification.stream().map(order -> {
            Optional<Field<?>> fieldOptional = getFieldWithName(order.getProperty());
            if (fieldOptional.isEmpty()) return null;

            if (order.isAscending()) {
                return fieldOptional.get()
                                    .asc();
            }

            return fieldOptional.get().desc();
        }).toList();
    }

    /**
     * Retrieve all moodels
     *
     * @return
     */
    @Override
    default Iterable<T> retrieveAll() {
        return getDsl().select().from(getTable())
                       .fetchInto(getModelClass());
    }

    default Page<T> retrieveAll(Pageable pageable) {
        var results = getDsl().select()
                              .from(getTable())
                              .orderBy(getSortFields(pageable.getSort()))
                              .limit(pageable.getPageSize())
                              .offset(pageable.getOffset())
                              .fetchInto(getModelClass());

        return new PageImpl<>(results, pageable, count());
    }

    default Iterable<T> retrieveAll(Collection<? extends OrderField<?>> orderFields) {
        return getDsl().select().from(getTable())
                       .orderBy(orderFields)
                       .fetchInto(getModelClass());
    }

    /**
     * Retrieve all models with id in [ids]
     *
     * @param ids
     * @return
     */
    @Override
    default Iterable<T> retrieveAllById(Iterable<I> ids) {
        return getDsl().select().from(getTable())
                       .where(getIdEqCondition(ids))
                       .fetchInto(getModelClass());
    }

    /**
     * Get the id from a model
     *
     * @param model
     * @return
     */
    I getId(T model);

    /**
     * Delete model
     *
     * @param model
     */
    @Override
    default void delete(T model) {
        deleteById(getId(model));
    }

    /**
     * Delete model linked to [id]
     *
     * @param id
     */
    @Override
    default void deleteById(I id) {
        int result = getDsl().delete(getTable())
                             .where(getIdEqCondition(id))
                             .execute();
    }

    /**
     * Delete multiple models in one network request
     *
     * @param models
     */
    @Override
    default void deleteAll(Iterable<T> models) {
        List<I> ids = StreamSupport.stream(models.spliterator(), false)
                                   .map(this::getId)
                                   .filter(Objects::nonNull)
                                   .toList();
        deleteAllById(ids);
    }

    /**
     * Delete all by id
     *
     * @param ids
     */
    @Override
    default void deleteAllById(Iterable<I> ids) {
        int result = getDsl().delete(getTable())
                             .where(getIdEqCondition(ids))
                             .execute();
    }

    default void delete(Condition condition) {
        int result = getDsl().delete(getTable())
                             .where(condition)
                             .execute();
    }
}
