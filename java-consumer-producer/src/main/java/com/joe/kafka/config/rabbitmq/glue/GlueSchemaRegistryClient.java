package com.joe.kafka.config.rabbitmq.glue;

import com.amazonaws.services.schemaregistry.common.AWSSchemaRegistryClient;
import com.amazonaws.services.schemaregistry.common.configs.GlueSchemaRegistryConfiguration;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.schema.registry.SchemaReference;
import org.springframework.cloud.stream.schema.registry.SchemaRegistrationResponse;
import org.springframework.cloud.stream.schema.registry.client.SchemaRegistryClient;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.glue.GlueClient;
import software.amazon.awssdk.services.glue.model.GetSchemaVersionRequest;
import software.amazon.awssdk.services.glue.model.GetSchemaVersionResponse;
import software.amazon.awssdk.services.glue.model.SchemaId;
import software.amazon.awssdk.services.glue.model.SchemaVersionNumber;

import java.util.Collections;
import java.util.UUID;

@Component
@AllArgsConstructor
@Slf4j
public class GlueSchemaRegistryClient implements SchemaRegistryClient {
    private final GlueClient glueClient;
    private final AWSSchemaRegistryClient awsSchemaRegistryClient;
    private final GlueSchemaRegistryConfiguration glueSchemaRegistryConfiguration;

    @Override
    public SchemaRegistrationResponse register(String subject, String format, String schema) {
        UUID schemaId = awsSchemaRegistryClient.createSchema(subject, format.toUpperCase(), schema, Collections.emptyMap());
        GetSchemaVersionResponse schemaVersionResponse = awsSchemaRegistryClient.getSchemaVersionResponse(schemaId.toString());

        SchemaRegistrationResponse response = new SchemaRegistrationResponse();
        response.setId(7);
        response.setSchemaReference(new SchemaReference(subject, schemaVersionResponse.versionNumber().intValue(), format));
        return response;
    }

    @Override
    public String fetch(SchemaReference schemaReference) {
        GetSchemaVersionResponse getSchemaVersionResponse = glueClient.getSchemaVersion(GetSchemaVersionRequest.builder()
                .schemaId(SchemaId.builder()
                        .registryName(glueSchemaRegistryConfiguration.getRegistryName())
                        .schemaName(schemaReference.getSubject())
                        .build())
                .schemaVersionNumber(SchemaVersionNumber.builder()
                        .versionNumber((long) schemaReference.getVersion())
                        .build())
                .build());

        return getSchemaVersionResponse.schemaDefinition();
    }

    @Override
    public String fetch(int id) {
        log.info("Shouldn't get called");
        return null;
    }

}
