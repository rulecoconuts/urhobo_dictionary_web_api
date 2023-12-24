package com.fejiro.exploration.dictionary.dictionary_web_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class DictionaryWebApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(DictionaryWebApiApplication.class, args);
    }

}
