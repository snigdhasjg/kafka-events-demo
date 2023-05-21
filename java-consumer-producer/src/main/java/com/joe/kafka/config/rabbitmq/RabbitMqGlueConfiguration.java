package com.joe.kafka.config.rabbitmq;

import com.joe.kafka.config.rabbitmq.glue.RabbitMqGlueAvroSerializer;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;

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
    public MessageConverter jsonMessageConverter(AwsCredentialsProvider awsCredentialProvider) {
        return new RabbitMqGlueAvroSerializer(awsCredentialProvider, rabbitmqConfigProperties());
    }
}
