package com.joe.kafka.consumer;

import com.joe.kafka.customer.Key;
import com.joe.kafka.customer.Value;
import lombok.extern.slf4j.Slf4j;
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
