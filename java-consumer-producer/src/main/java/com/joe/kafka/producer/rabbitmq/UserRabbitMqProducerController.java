package com.joe.kafka.producer.rabbitmq;

import com.joe.kafka.customer.UserValue;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@AllArgsConstructor
@Profile("rabbit-mq")
public class UserRabbitMqProducerController {
    @Value("${rabbitmq.producer-queue.user.exchange}")
    private final String exchange;
    @Value("${rabbitmq.producer-queue.user.routing-key}")
    private final String routingKey;
    private final RabbitTemplate rabbitTemplate;

    /**
     {
         "username": "ram",
         "name": "Ram Ghosh",
         "email": "ram@ghosh.in",
         "phoneNumber": "+919876543210",
         "countryIso": "IN",
         "deleted": false
     }
     */
    @PostMapping("/rabbitmq")
    public void sendMessage(@RequestBody UserValue customer) {
        log.info("Message sent {}", customer);
        rabbitTemplate.convertAndSend(exchange, routingKey, customer);
    }
}
