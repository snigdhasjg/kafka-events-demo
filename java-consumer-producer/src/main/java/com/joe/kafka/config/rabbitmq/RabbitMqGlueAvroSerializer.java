package com.joe.kafka.config.rabbitmq;

import com.amazonaws.services.schemaregistry.common.AWSDeserializerInput;
import com.amazonaws.services.schemaregistry.common.AWSSerializerInput;
import com.amazonaws.services.schemaregistry.common.configs.GlueSchemaRegistryConfiguration;
import com.amazonaws.services.schemaregistry.deserializers.GlueSchemaRegistryDeserializationFacade;
import com.amazonaws.services.schemaregistry.serializers.GlueSchemaRegistrySerializationFacade;
import com.amazonaws.services.schemaregistry.utils.GlueSchemaRegistryUtils;
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
    private final String schemaName;

    public RabbitMqGlueAvroSerializer(AwsCredentialsProvider awsCredentialsProvider, Properties properties) {
        Map<String, ?> configs = getMapFromPropertiesFile(properties);
        this.schemaName = GlueSchemaRegistryUtils.getInstance().getSchemaName(configs);
        GlueSchemaRegistryConfiguration glueSchemaRegistryConfiguration = new GlueSchemaRegistryConfiguration(configs);
        glueSchemaRegistryConfiguration.setUserAgentApp("rabbitmq");

        this.serializationFacade = GlueSchemaRegistrySerializationFacade.builder()
                .credentialProvider(awsCredentialsProvider)
                .glueSchemaRegistryConfiguration(glueSchemaRegistryConfiguration)
                .build();
        this.deserializationFacade = new GlueSchemaRegistryDeserializationFacade(glueSchemaRegistryConfiguration, awsCredentialsProvider);
    }

    @NotNull
    @Override
    protected Message createMessage(@NotNull Object data, @NotNull MessageProperties messageProperties) {
        AWSSerializerInput awsSerializerInput = AWSSerializerInput.builder()
                .dataFormat(DataFormat.AVRO.toString())
                .transportName(schemaName)
                .schemaName(schemaName)
                .schemaDefinition(serializationFacade.getSchemaDefinition(DataFormat.AVRO, data))
                .build();
        UUID schemaVersionIdFromRegistry = serializationFacade.getOrRegisterSchemaVersion(awsSerializerInput);
        byte[] body = serializationFacade.serialize(DataFormat.AVRO, data, schemaVersionIdFromRegistry);
        return new Message(body, messageProperties);
    }

    @NotNull
    @Override
    public Object fromMessage(@NotNull Message message) throws MessageConversionException {
        byte[] data = message.getBody();
        AWSDeserializerInput awsDeserializerInput = AWSDeserializerInput.builder()
                .transportName(schemaName)
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
