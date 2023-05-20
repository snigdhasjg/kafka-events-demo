package com.joe.kafka.consumer.rabbitmq;

import com.joe.kafka.customer.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("rabbit-mq")
@Slf4j
public class CustomerRabbitMqConsumer {
    @RabbitListener(queues = "${rabbitmq.queue.name}")
    public void consume(Value customer) {
        log.info("Received message {}", customer);
    }
}
