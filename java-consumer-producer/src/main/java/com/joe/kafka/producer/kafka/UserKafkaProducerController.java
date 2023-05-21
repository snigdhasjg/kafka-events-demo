package com.joe.kafka.producer.kafka;

import com.joe.kafka.customer.UserKey;
import com.joe.kafka.customer.UserValue;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@Slf4j
@AllArgsConstructor
@Profile({"aws-glue", "confluent-schema-registry"})
public class UserKafkaProducerController {

    @Value("${kafka.topic.name}")
    private final String topicName;
    private final KafkaTemplate<UserKey, UserValue> kafkaTemplate;

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
    @PostMapping("/kafka")
    public void produce(@RequestBody UserValue userValue) {
        UserKey userKey = new UserKey(UUID.randomUUID().toString());
        log.info("Message sent key: {}, value: {}", userKey, userValue);
        kafkaTemplate.send(topicName, userKey, userValue);
    }
}
