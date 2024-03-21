package com.fejiro.exploration.dictionary.dictionary_web_api.service.translation;

import com.fejiro.exploration.dictionary.dictionary_web_api.service.language.LanguageDomainObject;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class TranslationContext implements Serializable {

    @EqualsAndHashCode.Include
    LanguageDomainObject source;

    @EqualsAndHashCode.Include
    LanguageDomainObject target;
}
