package com.fejiro.exploration.dictionary.dictionary_web_api.data_transfer.word;

import com.fejiro.exploration.dictionary.dictionary_web_api.service.translation.TranslationContext;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class WordCreationRequestDTO implements Serializable {
    String name;
    TranslationContext translationContext;
    List<WordCreationWordPartSpecificationDTO> parts = new ArrayList<>();
}
