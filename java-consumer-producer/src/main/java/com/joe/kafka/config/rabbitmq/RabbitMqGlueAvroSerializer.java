package com.joe.kafka.config.rabbitmq;

import com.amazonaws.services.schemaregistry.common.AWSDeserializerInput;
import com.amazonaws.services.schemaregistry.common.AWSSerializerInput;
import com.amazonaws.services.schemaregistry.common.configs.GlueSchemaRegistryConfiguration;
import com.amazonaws.services.schemaregistry.deserializers.GlueSchemaRegistryDeserializationFacade;
import com.amazonaws.services.schemaregistry.exception.AWSSchemaRegistryException;
import com.amazonaws.services.schemaregistry.serializers.GlueSchemaRegistrySerializationFacade;
import org.apache.avro.generic.GenericContainer;
import org.jetbrains.annotations.NotNull;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.AbstractMessageConverter;
import org.springframework.amqp.support.converter.MessageConversionException;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.services.glue.model.DataFormat;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.stream.Collectors;

public class RabbitMqGlueAvroSerializer extends AbstractMessageConverter {
    private final GlueSchemaRegistrySerializationFacade serializationFacade;
    private final GlueSchemaRegistryDeserializationFacade deserializationFacade;
    private final String registryName;
    private final String awsRegion;

    public RabbitMqGlueAvroSerializer(AwsCredentialsProvider awsCredentialsProvider, Map<String, ?> configs) {
        GlueSchemaRegistryConfiguration glueSchemaRegistryConfiguration = new GlueSchemaRegistryConfiguration(configs);
        this.registryName = glueSchemaRegistryConfiguration.getRegistryName();
        this.awsRegion = glueSchemaRegistryConfiguration.getRegion();

        this.serializationFacade = GlueSchemaRegistrySerializationFacade.builder()
                .credentialProvider(awsCredentialsProvider)
                .glueSchemaRegistryConfiguration(glueSchemaRegistryConfiguration)
                .build();
        this.deserializationFacade = new GlueSchemaRegistryDeserializationFacade(glueSchemaRegistryConfiguration, awsCredentialsProvider);
    }

    @NotNull
    @Override
    protected Message createMessage(@NotNull Object data, @NotNull MessageProperties messageProperties) {
        String schemaName;
        if (data instanceof GenericContainer avroContainer) {
            schemaName = avroContainer.getSchema().getFullName();
        } else {
            throw new AWSSchemaRegistryException(String.format("Unsupported type passed for serialization: %s", data));
        }
        AWSSerializerInput awsSerializerInput = AWSSerializerInput.builder()
                .dataFormat(DataFormat.AVRO.toString())
                .transportName(schemaName)
                .schemaName(schemaName)
                .schemaDefinition(serializationFacade.getSchemaDefinition(DataFormat.AVRO, data))
                .build();
        messageProperties.setHeader("schema-name", schemaName);
        messageProperties.setHeader("registry-name", registryName);
        messageProperties.setHeader("aws-region", awsRegion);
        UUID schemaVersionIdFromRegistry = serializationFacade.getOrRegisterSchemaVersion(awsSerializerInput);
        byte[] body = serializationFacade.serialize(DataFormat.AVRO, data, schemaVersionIdFromRegistry);
        return new Message(body, messageProperties);
    }

    @NotNull
    @Override
    public Object fromMessage(@NotNull Message message) throws MessageConversionException {
        byte[] data = message.getBody();
        AWSDeserializerInput awsDeserializerInput = AWSDeserializerInput.builder()
                .buffer(ByteBuffer.wrap(data))
                .build();
        return deserializationFacade.deserialize(awsDeserializerInput);
    }

    private Map<String, ?> getMapFromPropertiesFile(Properties properties) {
        return properties.entrySet()
                .stream()
                .collect(Collectors.toMap(e -> e.getKey().toString(), Map.Entry::getValue));
    }

}
