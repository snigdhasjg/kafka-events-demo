package com.joe.kafka.producer.kafka;

import com.joe.kafka.customer.Key;
import com.joe.kafka.customer.Value;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@Slf4j
@AllArgsConstructor
@Profile("aws-glue")
public class CustomerKafkaProducerController {

    private final KafkaTemplate<Key, Value> kafkaTemplate;

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
    @PostMapping("/kafka")
    public void produce(@RequestBody Value customerValue) {
        Key customerKey = new Key(UUID.randomUUID().toString());
        log.info("Message sent key: {}, value: {}", customerKey, customerValue);
        kafkaTemplate.send("customer", customerKey, customerValue);
    }
}
