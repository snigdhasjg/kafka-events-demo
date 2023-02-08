package com.joe.connect.transformer;

import io.debezium.config.Configuration;
import io.debezium.config.Field;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.ConnectRecord;
import org.apache.kafka.connect.data.ConnectSchema;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.errors.ConnectException;
import org.apache.kafka.connect.transforms.Transformation;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class SchemaNameRegexRouter<R extends ConnectRecord<R>> implements Transformation<R> {

    private static final Field REGEX = Field.create("regex")
            .withDisplayName("Regular expression")
            .withType(ConfigDef.Type.STRING)
            .withDescription("Regular expression to use for matching.");

    private static final Field REPLACEMENT = Field.create("replacement")
            .withDisplayName("Replacement string")
            .withType(ConfigDef.Type.STRING)
            .withDescription("Replacement string.");

    private Pattern regex;
    private String replacement;

    @Override
    public void configure(Map<String, ?> props) {
        final Configuration config = Configuration.from(props);

        final Field.Set configFields = Field.setOf(REGEX, REPLACEMENT);
        if (!config.validateAndRecord(configFields, log::error)) {
            throw new ConnectException("Unable to validate config.");
        }

        this.regex = Pattern.compile(config.getString(REGEX));
        this.replacement = config.getString(REPLACEMENT);
    }

    @Override
    public R apply(R record) {
        Schema keySchema = updateSchema(record.keySchema());
        Schema valueSchema = updateSchema(record.valueSchema());
        return record.newRecord(
                record.topic(),
                record.kafkaPartition(),
                keySchema,
                updateSchemaIn(record.key(), keySchema),
                valueSchema,
                updateSchemaIn(record.value(), valueSchema),
                record.timestamp()
        );
    }

    private String replace(String schemaName) {
        if (schemaName == null) {
            log.trace("Not rerouting schema name as it is null");
            return null;
        }
        final Matcher matcher = regex.matcher(schemaName);
        if (matcher.matches()) {
            final String updatedSchemaName = matcher.replaceFirst(replacement);
            log.trace("Rerouting schema name from '{}' to new schema name '{}'", schemaName, updatedSchemaName);
            return updatedSchemaName;
        } else {
            log.trace("Not rerouting schema name '{}' as it does not match the configured regex", schemaName);
        }
        return schemaName;
    }

    private Schema updateSchema(Schema schema) {
        if (schema == null) {
            return null;
        }
        final boolean isArray = schema.type() == Schema.Type.ARRAY;
        final boolean isMap = schema.type() == Schema.Type.MAP;
        return new ConnectSchema(
                schema.type(),
                schema.isOptional(),
                schema.defaultValue(),
                replace(schema.name()),
                schema.version(),
                schema.doc(),
                schema.parameters(),
                schema.fields(),
                isMap ? schema.keySchema() : null,
                isMap || isArray ? schema.valueSchema() : null
        );
    }

    @Override
    public void close() {
    }

    @Override
    public ConfigDef config() {
        return Field.group(new ConfigDef(), null, REGEX, REPLACEMENT);
    }

    private Object updateSchemaIn(Object keyOrValue, Schema updatedSchema) {
        if (keyOrValue instanceof Struct) {
            Struct origStruct = (Struct) keyOrValue;
            Struct newStruct = new Struct(updatedSchema);
            for (org.apache.kafka.connect.data.Field field : updatedSchema.fields()) {
                newStruct.put(field, origStruct.get(field));
            }
            return newStruct;
        }
        return keyOrValue;
    }

}
