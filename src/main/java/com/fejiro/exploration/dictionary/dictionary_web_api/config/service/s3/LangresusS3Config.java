package com.fejiro.exploration.dictionary.dictionary_web_api.config.service.s3;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.io.Serializable;
import java.time.Duration;

@Data
@Configuration
@ConfigurationProperties(prefix = "langresus.s3")
public class LangresusS3Config implements Serializable {
    String bucket;
    Integer minObjectSuffixLength;
    Integer maxObjctSuffixLength;
    String presignedUploadUrlLifeTime;
    String environment;

    public Duration getPresignedUploadUrlLifeTimeDuration() {
        return Duration.parse(presignedUploadUrlLifeTime);
    }
}
