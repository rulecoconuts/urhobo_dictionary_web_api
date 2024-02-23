package com.fejiro.exploration.dictionary.dictionary_web_api.service;

import com.fejiro.exploration.dictionary.dictionary_web_api.database.CRUDDAO;
import com.fejiro.exploration.dictionary.dictionary_web_api.database.GenericJOOQCRUDDAO;
import com.fejiro.exploration.dictionary.dictionary_web_api.database.TemporalAuditable;
import com.fejiro.exploration.dictionary.dictionary_web_api.database.UserAuditable;
import com.fejiro.exploration.dictionary.dictionary_web_api.error_handling.ApiExceptionWithComplexObjectMessageMap;
import com.fejiro.exploration.dictionary.dictionary_web_api.error_handling.IllegalArgumentExceptionWithMessageMap;
import com.fejiro.exploration.dictionary.dictionary_web_api.service.user.AppUserDataObject;
import com.fejiro.exploration.dictionary.dictionary_web_api.service.user.AppUserDomainObject;
import org.jooq.Condition;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;
import java.util.stream.StreamSupport;

public interface GenericJOOQBackedService<T, D, I> extends CRUDService<T, I>, ConversionServiceDataBackedService<T, D> {
    CRUDDAO<D, I> getCRUDAO();

    @Override
    default T create(T model) throws IllegalArgumentExceptionWithMessageMap {
        throwIfModelIsInvalidForCreation(model);

        T preProcessedModel = preProcessBeforeCreation(model);

        D dataObject = toData(preProcessedModel);

        // Create data record
        return toDomain(getCRUDAO().create(dataObject));
    }

    default GenericJOOQCRUDDAO<D, I, ?> getGenericJOOQDAO() {
        return ((GenericJOOQCRUDDAO<D, I, ?>) getCRUDAO());
    }

    default T preProcessBeforeCreation(T model) {
        return model;
    }

    @Override
    default Map<String, String> validateModelForCreation(T model) {
        return new HashMap<>();
    }

    @Override
    default void throwIfModelIsInvalidForCreation(T model) throws IllegalArgumentExceptionWithMessageMap {
        Map<String, String> errors = validateModelForCreation(model);
        if (!errors.isEmpty())
            throw new IllegalArgumentExceptionWithMessageMap("Error with creation of domain model", errors,
                                                             HttpStatus.BAD_REQUEST);
    }

    @Override
    default Iterable<T> createAll(
            Iterable<T> models) throws IllegalArgumentExceptionWithMessageMap, ApiExceptionWithComplexObjectMessageMap {
        // Validate
        throwIfModelsAreInvalidForCreation(models);

        // Preprocess
        Iterable<T> preProcessedModels = preProcessBeforeCreation(models);

        List<D> data = StreamSupport.stream(preProcessedModels.spliterator(), false)
                                    .map(this::toData)
                                    .toList();

        // Create
        return StreamSupport.stream(getCRUDAO().createAll(data).spliterator(), false)
                            .map(this::toDomain)
                            .toList();
    }

    default Iterable<T> preProcessBeforeCreation(Iterable<T> models) {
        return StreamSupport.stream(models.spliterator(), false)
                            .map(this::preProcessBeforeCreation)
                            .toList();
    }


    @Override
    default Map<T, Map<String, String>> validateModelsForCreation(Iterable<T> models) {
        return new HashMap<>();
    }

    @Override
    default void throwIfModelsAreInvalidForCreation(Iterable<T> models) throws ApiExceptionWithComplexObjectMessageMap {
        Map<T, Map<String, String>> errors = validateModelsForCreation(models);
        if (errors.isEmpty()) return;

        throw new ApiExceptionWithComplexObjectMessageMap("Error while creating domain models",
                                                          HttpStatus.BAD_REQUEST,
                                                          errors,
                                                          (model) -> generateErrorLabel((T) model));

    }

    String generateErrorLabel(T model);

    @Override
    default Map<String, String> validateModelForUpdate(T model, Optional<T>
            existingCopy) {
        return new HashMap<>();
    }

    @Override
    default void throwIfModelIsInvalidForUpdate(T model,
                                                Optional<T> existingCopy) throws IllegalArgumentExceptionWithMessageMap {
        Map<String, String> errors = validateModelForUpdate(model, existingCopy);
        if (!errors.isEmpty())
            throw new IllegalArgumentExceptionWithMessageMap("Error with update of domain model", errors,
                                                             HttpStatus.BAD_REQUEST);
    }

    default T copyModel(T model) {
        return model;
    }

    default T preProcessBeforeUpdate(T model, Optional<T> existingCopy) {
        var newModel = copyModel(model);

        if (newModel instanceof TemporalAuditable) {
            ((TemporalAuditable) newModel).setCreatedAt(((TemporalAuditable) existingCopy.get()).getCreatedAt());
        }

        if (newModel instanceof UserAuditable) {
            ((UserAuditable) newModel).setCreatedBy(((UserAuditable) existingCopy.get()).getCreatedBy());
        }
        return newModel;
    }

    I getId(T model);

    default Iterable<T> preProcessBeforeUpdate(Iterable<T> models, Collection<T> existingCopies) {
        return StreamSupport.stream(models.spliterator(), false)
                            .map(model -> {
                                var exisitingCopy = existingCopies.stream().filter(c -> getId(c).equals(getId(model)))
                                                                  .findFirst();
                                if (exisitingCopy.isEmpty()) return Optional.<T>empty();
                                return Optional.ofNullable(preProcessBeforeUpdate(model, exisitingCopy));
                            })
                            .filter(Optional::isPresent)
                            .map(Optional::get)
                            .toList();
    }

    /**
     * Get the copy of a model that exists in the database
     *
     * @param model
     * @return
     */
    default Optional<T> getExistingCopy(T model) {
        return retrieveById(getId(model));
    }

    @Override
    default T update(T model) throws IllegalArgumentExceptionWithMessageMap {
        Optional<T> existingCopy = getExistingCopy(model);
        throwIfModelIsInvalidForUpdate(model, existingCopy);

        T preProcessedModel = preProcessBeforeUpdate(model, existingCopy);

        D dataObject = toData(preProcessedModel);

        // Update data record
        return toDomain(getCRUDAO().update(dataObject));
    }

    @Override
    default Iterable<T> updateAll(Iterable<T> models) {
        return null;
    }

    @Override
    default Map<T, Map<String, String>> validateModelsForUpdate(Iterable<T> models, Iterable<T> existingCopies) {
        return new HashMap<>();
    }

    @Override
    default void throwIfModelsAreInvalidForUpdate(Iterable<T> models,
                                                  Iterable<T> existingCopies) throws ApiExceptionWithComplexObjectMessageMap {
        Map<T, Map<String, String>> errors = validateModelsForUpdate(models, existingCopies);
        if (errors.isEmpty()) return;

        throw new ApiExceptionWithComplexObjectMessageMap("Error while updating domain models",
                                                          HttpStatus.BAD_REQUEST,
                                                          errors,
                                                          (model) -> generateErrorLabel((T) model));
    }

    @Override
    default void delete(T model) {
        getCRUDAO().delete(toData(model));
    }

    @Override
    default void deleteById(I id) {
        getCRUDAO().deleteById(id);
    }

    @Override
    default void deleteAll(Iterable<T> models) {
        getCRUDAO().deleteAll(StreamSupport.stream(models.spliterator(), false)
                                           .map(this::toData)
                                           .toList());
    }

    @Override
    default void deleteAllById(Iterable<I> ids) {
        getCRUDAO().deleteAllById(ids);
    }

    @Override
    default Optional<T> retrieveById(I id) {
        return getCRUDAO().retrieveById(id)
                          .map(this::toDomain);
    }

    @Override
    default Iterable<T> retrieveAll() {
        return StreamSupport.stream(getCRUDAO().retrieveAll().spliterator(), false)
                            .map(this::toDomain)
                            .toList();
    }

    @Override
    default Iterable<T> retrieveAllById(Iterable<I> ids) {
        return StreamSupport.stream(getCRUDAO().retrieveAllById(ids).spliterator(), false)
                            .map(this::toDomain)
                            .toList();
    }

    default Iterable<T> retrieveAll(Condition condition) {
        return StreamSupport.stream(((GenericJOOQCRUDDAO<D, I, ?>) getCRUDAO()).retrieveAll(condition).spliterator(),
                                    false)
                            .map(this::toDomain)
                            .toList();
    }

    default Optional<T> retrieveOne(Condition condition) {
        return ((GenericJOOQCRUDDAO<D, I, ?>) getCRUDAO()).retrieveOne(condition)
                                                          .map(this::toDomain);
    }


}
