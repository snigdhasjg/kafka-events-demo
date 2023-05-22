package com.joe.kafka.config.rabbitmq;

import com.amazonaws.services.schemaregistry.common.AWSSchemaRegistryClient;
import com.amazonaws.services.schemaregistry.common.configs.GlueSchemaRegistryConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.glue.GlueClient;

import java.util.HashMap;
import java.util.Map;

@Configuration
@Profile("rabbit-mq")
public class RabbitMqGlueConfiguration {

    @Bean
    @ConfigurationProperties("rabbitmq.properties")
    public Map<String, ?> rabbitmqConfigProperties() {
        return new HashMap<>();
    }

    @Bean
    public GlueSchemaRegistryConfiguration glueSchemaRegistryConfiguration(Map<String, ?> rabbitmqConfigProperties) {
        return new GlueSchemaRegistryConfiguration(rabbitmqConfigProperties);
    }

    @Bean
    public GlueClient glueClient(AwsCredentialsProvider awsCredentialProvider, GlueSchemaRegistryConfiguration glueSchemaRegistryConfiguration) {
        return GlueClient.builder()
                .credentialsProvider(awsCredentialProvider)
                .region(Region.of(glueSchemaRegistryConfiguration.getRegion()))
                .region(Region.AP_SOUTH_1)
                .build();
    }

    @Bean
    public AWSSchemaRegistryClient awsSchemaRegistryClient(AwsCredentialsProvider awsCredentialProvider, GlueSchemaRegistryConfiguration glueSchemaRegistryConfiguration) {
        return new AWSSchemaRegistryClient(awsCredentialProvider, glueSchemaRegistryConfiguration);
    }
}
