package com.joe.kafka.config.rabbitmq;

import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;

import java.util.Optional;
import java.util.Properties;

@Configuration
@Profile("rabbit-mq")
public class CustomerQueueConfiguration {
    @Value("${rabbitmq.queue.name}")
    private String queueName;

    @Value("${rabbitmq.exchange}")
    private String exchange;

    @Value("${rabbitmq.routing-key}")
    private String routingKey;

    @Bean
    public Queue queue() {
        return new Queue(queueName, true);
    }

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(exchange);
    }

    @Bean
    public Binding binding() {
        return BindingBuilder
                .bind(queue())
                .to(exchange())
                .with(routingKey);
    }

    @Bean
    @ConfigurationProperties("rabbitmq.schema-properties")
    public Properties schemeProperties() {
        return new Properties();
    }

    @Bean
    public AwsCredentialsProvider awsCredentialProvider(@Value("${aws.auth.profile:}") String profileName) {
        return DefaultCredentialsProvider
                .builder()
                .profileName(Optional.ofNullable(profileName).filter(StringUtils::isNotBlank).orElse(null))
                .build();
    }

    @Bean
    public MessageConverter jsonMessageConverter(AwsCredentialsProvider awsCredentialProvider, Properties schemeProperties) {
        return new RabbitMqGlueAvroSerializer(awsCredentialProvider, schemeProperties);
    }
}
