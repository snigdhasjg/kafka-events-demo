package com.joe.kafka.config.aws;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;

import java.util.Optional;

@Configuration
@Profile({"rabbit-mq", "aws-glue"})
public class AwsConfiguration {
    @Bean
    public AwsCredentialsProvider awsCredentialProvider(@Value("${aws.auth.profile:}") String profileName) {
        return DefaultCredentialsProvider
                .builder()
                .profileName(Optional.ofNullable(profileName).filter(StringUtils::isNotBlank).orElse(null))
                .build();
    }
}
