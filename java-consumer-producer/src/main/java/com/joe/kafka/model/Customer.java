package com.joe.kafka.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Customer {
    String username;
    String name;
    String email;
    @JsonProperty("phone_number")
    String phone_number;
    @JsonProperty("country_iso")
    String country_iso;
    @JsonProperty("__deleted")
    boolean deleted;
}
