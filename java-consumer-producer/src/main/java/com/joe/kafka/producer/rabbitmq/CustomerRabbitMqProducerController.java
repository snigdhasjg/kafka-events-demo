package com.joe.kafka.producer.rabbitmq;

import com.joe.kafka.model.Customer;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Profile("rabbit-mq")
public class CustomerRabbitMqProducerController {
    @org.springframework.beans.factory.annotation.Value("${rabbitmq.exchange}")
    String exchange;
    @org.springframework.beans.factory.annotation.Value("${rabbitmq.routing-key}")
    String routingKey;
    RabbitTemplate rabbitTemplate;

    /**
     {
         "username": "ram",
         "name": "Ram Pal",
         "email": "ram@pal.in",
         "phone_number": "+919876543210",
         "country_iso": "IN",
         "__deleted": false
     }
     */
    @PostMapping("/rabbitmq")
    public void sendMessage(@RequestBody Customer customer) {
        log.info("Message sent {}", customer);
        rabbitTemplate.convertAndSend(exchange, routingKey, customer);
    }
}
