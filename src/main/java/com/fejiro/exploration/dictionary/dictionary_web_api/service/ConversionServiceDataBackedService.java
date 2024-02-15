package com.fejiro.exploration.dictionary.dictionary_web_api.service;

import com.fejiro.exploration.dictionary.dictionary_web_api.service.user.AppUserDataObject;
import com.fejiro.exploration.dictionary.dictionary_web_api.service.user.AppUserDomainObject;
import org.springframework.core.convert.ConversionService;

public interface ConversionServiceDataBackedService<T, D> extends DataBackedService<T, D> {
    ConversionService getConversionService();

    Class<? extends T> getDomainClass();

    Class<? extends D> getDataClass();

    @Override
    default T toDomain(D dataObject) {
        return getConversionService().convert(dataObject, getDomainClass());
    }

    @Override
    default D toData(T domain) {
        return getConversionService().convert(domain, getDataClass());
    }
}
