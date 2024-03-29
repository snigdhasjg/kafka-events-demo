-- https://debezium.io/documentation/reference/stable/connectors/postgresql.html#postgresql-connector-properties
CREATE SOURCE CONNECTOR POSTGRES_SOURCE_CONNECTOR WITH(
    'connector.class'='io.debezium.connector.postgresql.PostgresConnector',
    'tasks.max'=1,
    'plugin.name'='wal2json_rds_streaming',
    'slot.name'='wal2json_rds_streaming_name',

    'database.hostname'='${env:<ignore>:postgres_hostname}',
    'database.port'=5432,
    'database.user'='${env:<ignore>:postgres_user}',
    'database.password'='${env:<ignore>:postgres_password}',
    'database.dbname'='${env:<ignore>:postgres_dbname}',
    'database.server.id'=7,
    'database.server.name'='product',
    'schema.include.list'='master',
--     'snapshot.mode'='always',

    'internal.key.converter'='org.apache.kafka.connect.json.JsonConverter',
    'internal.key.converter.schemas.enable'='false',
    'internal.value.converter'='org.apache.kafka.connect.json.JsonConverter',
    'internal.value.converter.schemas.enable'='false',
    'key.converter'='io.confluent.connect.avro.AvroConverter',
    'key.converter.schemas.enable'=true,
    'key.converter.enhanced.avro.schema.support'=true,
    'key.converter.schema.registry.url'='${env:<ignore>:CONNECT_SCHEMA_REGISTRY_URL}',
    'value.converter'='io.confluent.connect.avro.AvroConverter',
    'value.converter.schemas.enable'=true,
    'value.converter.enhanced.avro.schema.support'=true,
    'value.converter.schema.registry.url'='${env:<ignore>:CONNECT_SCHEMA_REGISTRY_URL}',

    'transforms'='route',
    'transforms.route.type'='org.apache.kafka.connect.transforms.RegexRouter',
    'transforms.route.regex'='([^.]+)\.([^.]+)\.([^.]+)',
    'transforms.route.replacement'='$3',
    'tombstones.on.delete'=false,

    'topic.creation.default.cleanup.policy'='compact',
    'topic.creation.default.replication.factor'=-1,
    'topic.creation.default.partitions'=1
);
