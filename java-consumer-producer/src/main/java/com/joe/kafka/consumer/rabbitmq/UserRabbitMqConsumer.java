package com.joe.kafka.consumer.rabbitmq;

import com.joe.kafka.customer.UserValue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("rabbit-mq")
@Slf4j
public class UserRabbitMqConsumer {
    @RabbitListener(queues = "${rabbitmq.queue.user.name}")
    public void consume(UserValue customer) {
        log.info("Received message {}", customer);
    }
}
