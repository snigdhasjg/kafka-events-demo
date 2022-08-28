package com.joe.kafka.producer;

import com.joe.kafka.Customer;
import com.joe.kafka.customer.Key;
import com.joe.kafka.customer.Value;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@AllArgsConstructor
public class CustomerProducer {

    private final KafkaTemplate<Key, Value> kafkaTemplate;

    @PostMapping("/kafka")
    public void produce(@RequestBody Customer customer) {
        kafkaTemplate.send("customer", customer.getKey(), customer.getValue());
    }
}
