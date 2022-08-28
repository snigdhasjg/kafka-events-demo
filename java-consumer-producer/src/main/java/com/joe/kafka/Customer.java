package com.joe.kafka;

import com.joe.kafka.customer.Key;
import com.joe.kafka.customer.Value;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Customer {
    Key key;
    Value value;
}
