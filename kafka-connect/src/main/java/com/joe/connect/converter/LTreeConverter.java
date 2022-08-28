package com.joe.connect.converter;

import io.debezium.spi.converter.CustomConverter;
import io.debezium.spi.converter.RelationalColumn;
import org.apache.kafka.connect.data.SchemaBuilder;

import java.util.Properties;

public class LTreeConverter implements CustomConverter<SchemaBuilder, RelationalColumn> {
    private SchemaBuilder ltreeSchema;

    @Override
    public void configure(Properties props) {

    }

    @Override
    public void converterFor(RelationalColumn field, ConverterRegistration<SchemaBuilder> registration) {

    }
}
