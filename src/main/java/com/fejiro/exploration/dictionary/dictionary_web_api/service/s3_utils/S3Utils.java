package com.fejiro.exploration.dictionary.dictionary_web_api.service.s3_utils;

import com.fejiro.exploration.dictionary.dictionary_web_api.config.service.s3.LangresusS3Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;

@Component
public class S3Utils {
    @Autowired
    S3Client s3Client;
    @Autowired
    LangresusS3Config langresusS3Config;

    public String getKey(String url) {
        String searchString = ".amazonaws.com/";
        int index = url.indexOf(searchString);
        if (index < 0) return url;
        return url.substring(index + searchString.length());
    }

    public void deleteObject(String url) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                                                                     .bucket(langresusS3Config.getBucket())
                                                                     .key(getKey(
                                                                             url))
                                                                     .build();
        s3Client.deleteObject(deleteObjectRequest);
    }
}
