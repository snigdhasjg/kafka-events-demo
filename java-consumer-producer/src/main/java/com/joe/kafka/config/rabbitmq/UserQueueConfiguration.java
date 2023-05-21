package com.joe.kafka.config.rabbitmq;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("rabbit-mq")
@RequiredArgsConstructor
public class UserQueueConfiguration {

    @Value("${rabbitmq.queue.user.name}")
    private final String queueName;
    @Value("${rabbitmq.queue.user.exchange}")
    private final String exchange;
    @Value("${rabbitmq.queue.user.routing-key}")
    private final String routingKey;

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
        return BindingBuilder.bind(queue()).to(exchange())
                .with(routingKey);
    }
}
