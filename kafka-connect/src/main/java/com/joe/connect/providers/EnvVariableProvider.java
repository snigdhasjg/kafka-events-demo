package com.joe.connect.providers;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.config.ConfigData;
import org.apache.kafka.common.config.provider.ConfigProvider;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
public class EnvVariableProvider implements ConfigProvider {
    @Override
    public ConfigData get(String path) {
        //path variable is not being used here
        return new ConfigData(Map.of());
    }

    @Override
    public ConfigData get(String path, Set<String> keys) {
        //path variable is not being used here
        Function<String, String> valueMapper = key -> {
            String value = System.getenv(key);
            if (Objects.isNull(value)) {
                log.error("Failed to look up key '{}'. key not found", key);
                throw new RuntimeException(String.format("Failed to look up key '%s'. key not found", key));
            }
            return value;
        };
        Map<String, String> keyValuePairs = keys.stream().collect(Collectors.toMap(key -> key, valueMapper));
        return new ConfigData(keyValuePairs);
    }

    @Override
    public void close() {
        log.info("Closing environment config provider");
    }

    @Override
    public void configure(Map<String, ?> configs) {
        log.info("Configuring environment secret provider");
    }
}
