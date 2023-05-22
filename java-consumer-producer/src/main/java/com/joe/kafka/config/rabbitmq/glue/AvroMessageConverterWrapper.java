package com.joe.kafka.config.rabbitmq.glue;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.support.converter.MessagingMessageConverter;
import org.springframework.cloud.stream.schema.registry.avro.AvroSchemaRegistryClientMessageConverter;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
@RequiredArgsConstructor
public class AvroMessageConverterWrapper implements MessageConverter {

    private final AvroSchemaRegistryClientMessageConverter avroConverter;
    private final MessagingMessageConverter messagingMessageConverter = new MessagingMessageConverter();

    @Override
    public Message toMessage(Object object, MessageProperties messageProperties) throws MessageConversionException {
        org.springframework.messaging.Message<?> avroMessage = avroConverter.toMessage(object, new MessageHeaders(Collections.emptyMap()));
        return messagingMessageConverter.toMessage(avroMessage, messageProperties);
    }

    @Override
    public Object fromMessage(Message message) throws MessageConversionException {
        org.springframework.messaging.Message<?> avroMessage = (org.springframework.messaging.Message<?>) messagingMessageConverter.fromMessage(message);
        var inferredType = message.getMessageProperties().getInferredArgumentType();
        if (!(inferredType instanceof Class<?>)) {
            throw new RuntimeException("invalid argument type");
        }
        var targetClass = (Class<?>) message.getMessageProperties().getInferredArgumentType();
        return avroConverter.fromMessage(avroMessage, targetClass);
    }
}