package com.joe.kafka.consumer.rabbitmq;

import com.joe.kafka.customer.UserValue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("rabbit-mq")
@Slf4j
public class UserRabbitMqConsumer {
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue("${rabbitmq.consumer-queue.user.name}"),
            exchange = @Exchange("${rabbitmq.consumer-queue.user.exchange}"),
            key = "${rabbitmq.consumer-queue.user.routing-key}")
    )
    public void consume(UserValue customer, MessageProperties messageProperties) {
        log.info("Received message {}, with properties {}", customer, messageProperties);
    }
}
