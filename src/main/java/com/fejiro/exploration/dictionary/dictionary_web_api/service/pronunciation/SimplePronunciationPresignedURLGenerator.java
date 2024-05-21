package com.fejiro.exploration.dictionary.dictionary_web_api.service.pronunciation;

import com.fejiro.exploration.dictionary.dictionary_web_api.config.service.s3.LangresusS3Config;
import com.fejiro.exploration.dictionary.dictionary_web_api.data_transfer.word.PronunciationPresignResult;
import com.fejiro.exploration.dictionary.dictionary_web_api.service.user.AppUserDomainObject;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.regions.providers.AwsRegionProvider;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.OffsetDateTime;

@Component
public class SimplePronunciationPresignedURLGenerator implements PronunciationPresignedURLGenerator {
    @Autowired
    S3Client s3Client;

    @Autowired
    LangresusS3Config langresusS3Config;

    Logger logger = LoggerFactory.getLogger(SimplePronunciationPresignedURLGenerator.class);

    @Autowired
    AwsRegionProvider awsRegionProvider;

    @Override
    public PronunciationPresignResult generatePresignedUploadURL(PronunciationDomainObject pronunciation) {
        try (S3Presigner presigner = S3Presigner.create()) {

            PutObjectRequest objectRequest = PutObjectRequest.builder()
                                                             .bucket(langresusS3Config.getBucket())
                                                             .key(generateObjectUploadKey(pronunciation))
                                                             .acl(ObjectCannedACL.PUBLIC_READ)
                                                             .build();

            PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                                                                            .signatureDuration(
                                                                                    langresusS3Config.getPresignedUploadUrlLifeTimeDuration())
                                                                            .putObjectRequest(objectRequest)
                                                                            .build();

            PresignedPutObjectRequest presignedPutObjectRequest = presigner.presignPutObject(presignRequest);
            String url = presignedPutObjectRequest.url().toString();
            logger.debug(String.format("For pronunciation %s, generated presign url: %s", pronunciation.getWordPartId(),
                                       url));

            String destinationUrl = generateDestinationUrl(s3Client, objectRequest.bucket(), objectRequest.key());

            return PronunciationPresignResult.builder()
                                             .presignedUrl(presignedPutObjectRequest.url().toExternalForm())
                                             .destinationUrl(destinationUrl)
                                             .pronunciation(pronunciation)
                                             .build();
        }
    }

    String generateDestinationUrl(S3Client s3Client, String bucket, String key) {
        try {
//            var clientConfig = s3Client.serviceClientConfiguration();
            Region region = awsRegionProvider.getRegion();
            String regionString = "ca-central-1";
            StringBuilder builder = new StringBuilder("https://");
            builder.append(bucket);
            builder.append(".s3-");
            builder.append(regionString);
            builder.append(".amazonaws.com/");
            builder.append(key);

            return builder.toString();
        } catch (Exception e) {
            throw e;
        }
    }

    String generateObjectUploadKey(PronunciationDomainObject pronunciation) {
        AppUserDomainObject user = (AppUserDomainObject) SecurityContextHolder.getContext()
                                                                              .getAuthentication()
                                                                              .getPrincipal();
        StringBuilder keyBuilder = new StringBuilder(langresusS3Config.getEnvironment());
        keyBuilder.append("/");
        keyBuilder.append(user.getId().toString());
        keyBuilder.append("/");
        keyBuilder.append(pronunciation.getWordPartId());
        keyBuilder.append("_");
        keyBuilder.append(OffsetDateTime.now());
        keyBuilder.append("_");
        keyBuilder.append(RandomStringUtils.randomAlphabetic(langresusS3Config.getMinObjectSuffixLength(),
                                                             langresusS3Config.getMaxObjctSuffixLength()));
        return keyBuilder.toString();
    }
}
