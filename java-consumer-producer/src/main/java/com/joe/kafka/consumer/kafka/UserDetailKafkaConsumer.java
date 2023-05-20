package com.joe.kafka.consumer.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import product.master.user_detail.Envelope;
import product.master.user_detail.Key;

@Service
@Slf4j
@Profile("confluent-schema-registry")
public class UserDetailKafkaConsumer {

    @KafkaListener(topics = "user_detail")
    public void consume(ConsumerRecord<Key, Envelope> record) {
        log.info("Message received-> key: {}, value: {}", record.key(), record.value());
    }
}
