package com.joe.kafka.config.kafka.glue;

import com.amazonaws.services.schemaregistry.serializers.avro.AWSKafkaAvroSerializer;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.DefaultKafkaProducerFactoryCustomizer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;

import java.util.Map;
import java.util.Optional;

@Configuration
@AllArgsConstructor
@org.springframework.context.annotation.Profile("aws-glue")
public class KafkaGlueConfiguration {
    private final KafkaProperties properties;
    private final ObjectProvider<DefaultKafkaProducerFactoryCustomizer> customizers;

    @Bean
    public AwsCredentialsProvider awsCredentialProvider(@Value("${aws.auth.profile:}") String profileName) {
        return DefaultCredentialsProvider
                .builder()
                .profileName(Optional.ofNullable(profileName).filter(StringUtils::isNotBlank).orElse(null))
                .build();
    }

    @Bean
    public DefaultKafkaProducerFactory<?, ?> customerKafkaProducerFactory(AwsCredentialsProvider awsCredentialsProvider) {
        Map<String, Object> producerProperties = properties.buildProducerProperties();
        DefaultKafkaProducerFactory<?, ?> factory = new DefaultKafkaProducerFactory<>(
                producerProperties,
                () -> new AWSKafkaAvroSerializer(awsCredentialsProvider, null),
                () -> new AWSKafkaAvroSerializer(awsCredentialsProvider, null),
                true
        );
        String transactionIdPrefix = properties.getProducer().getTransactionIdPrefix();
        if (transactionIdPrefix != null) {
            factory.setTransactionIdPrefix(transactionIdPrefix);
        }
        customizers.orderedStream().forEach((customizer) -> customizer.customize(factory));
        return factory;
    }
}
