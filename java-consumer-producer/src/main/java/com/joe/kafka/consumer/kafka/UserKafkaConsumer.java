package com.joe.kafka.consumer.kafka;

import com.joe.kafka.customer.UserKey;
import com.joe.kafka.customer.UserValue;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@Profile({"aws-glue", "confluent-schema-registry"})
public class UserKafkaConsumer {

    @KafkaListener(topics = "${kafka.topic.name}")
    public void consume(ConsumerRecord<UserKey, UserValue> record) {
        log.info("Message received-> key: {}, value: {}", record.key(), record.value());
    }
}
