package com.fejiro.exploration.dictionary.dictionary_web_api.database;

import lombok.Builder;
import lombok.NonNull;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Table;
import org.jooq.UpdatableRecord;

import java.util.List;
import java.util.function.Function;

@Builder
public class ConfigurableGenericJOOQCRUDDAO<T, I, R extends UpdatableRecord<R>> implements GenericJOOQCRUDDAO<T, I, R> {
    @NonNull
    final DSLContext dslContext;

    @NonNull
    final Class<T> modelClass;

    final Function<T, String> validateForCreationFunction;

    final Function<T, String> validateForUpdateFunction;

    @NonNull
    final Table<R> table;

    @NonNull
    final Function<I, Condition> idMatchConditionGenerator;

    @NonNull
    final Function<Iterable<I>, Condition> idCollectionMatchConditionGenerator;

    @NonNull
    final Function<T, I> idExtractionFunction;

    final Function<T, T> creationPreProcessFunction;

    final Function<T, T> updatePreProcessFunction;


    @Override
    public T preProcessDataForCreation(T model) {
        if (creationPreProcessFunction == null)
            return GenericJOOQCRUDDAO.super.preProcessDataForCreation(model);
        return creationPreProcessFunction.apply(model);
    }

    @Override
    public T preProcessDataForUpdate(T model) {
        if (updatePreProcessFunction == null)
            return GenericJOOQCRUDDAO.super.preProcessDataForUpdate(model);
        return updatePreProcessFunction.apply(model);
    }

    @Override
    public DSLContext getDsl() {
        return dslContext;
    }

    @Override
    public Class<T> getModelClass() {
        return modelClass;
    }

    @Override
    public String validateForCreation(T model) {
        if (validateForCreationFunction == null)
            return GenericJOOQCRUDDAO.super.validateForCreation(model);
        return validateForCreationFunction.apply(model);
    }

    @Override
    public Table<R> getTable() {
        return table;
    }

    @Override
    public String validateForUpdate(T model) {
        if (validateForUpdateFunction == null)
            return GenericJOOQCRUDDAO.super.validateForUpdate(model);
        return validateForUpdateFunction.apply(model);
    }

    @Override
    public Condition getIdEqCondition(I id) {
        return idMatchConditionGenerator.apply(id);
    }

    @Override
    public Condition getIdEqCondition(Iterable<I> ids) {
        return idCollectionMatchConditionGenerator.apply(ids);
    }

    @Override
    public I getId(T model) {
        return idExtractionFunction.apply(model);
    }
}
