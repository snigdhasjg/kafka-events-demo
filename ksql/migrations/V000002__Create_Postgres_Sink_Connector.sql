CREATE
SINK CONNECTOR PRODUCTS_SINK_CONNECTOR
WITH (
   'connector.class' = 'io.confluent.connect.jdbc.JdbcSinkConnector',
   'connection.url' = 'jdbc:postgresql://${env:<ignore>:hostname}:5432/${env:<ignore>:dbname}?currentSchema=master',
   'connection.user' = '${env:<ignore>:user}',
   'connection.password' = '${env:<ignore>:password}',
   'topics' = 'product',
   'max.retries'= 3,
   'auto.create' = 'true',
   'insert.mode' = 'upsert',
   'pk.mode' = 'record_key',
   'pk.fields' = 'CODE',
   'delete.enabled'= 'true',
   'value.converter.schema.registry.url'='${env:<ignore>:CONNECT_SCHEMA_REGISTRY_URL}',
   'key.converter.schema.registry.url'='${env:<ignore>:CONNECT_SCHEMA_REGISTRY_URL}',
   'key.converter'='io.confluent.connect.avro.AvroConverter',
   'value.converter'='io.confluent.connect.avro.AvroConverter',
   'quote.sql.identifiers'='never',
   'errors.tolerance'='all',
   'errors.deadletterqueue.topic.name' = 'product_dlq',
   'errors.deadletterqueue.topic.replication.factor'= -1,
   'errors.log.include.messages' = true,
   'errors.log.enable' = true,
   'errors.deadletterqueue.context.headers.enable' = true
);