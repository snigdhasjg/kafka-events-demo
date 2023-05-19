package com.joe.kafka.config.aws.glue;

import com.amazonaws.services.schemaregistry.common.AWSSchemaNamingStrategy;

public class KafkaAvroNamingStrategy implements AWSSchemaNamingStrategy {

    @Override
    public String getSchemaName(String transportName, Object data, boolean isKey) {
        String suffix = isKey ? "-key" : "-value";
        return transportName + suffix;
    }

    @Override
    public String getSchemaName(String transportName) {
        return transportName;
    }
}
