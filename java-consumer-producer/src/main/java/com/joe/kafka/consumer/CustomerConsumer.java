package com.joe.kafka.kafka.consumer;

import lombok.extern.slf4j.Slf4j;
import mysql.root.customer.Key;
import mysql.root.customer.Value;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CustomerConsumer {

    @KafkaListener(topics = "customer")
    public void consume(ConsumerRecord<Key, Value> record) {
        log.info("Message received-> key: {}, value: {}", record.key(), record.value());
    }
}
