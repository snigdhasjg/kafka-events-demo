DROP TABLE IF EXISTS customer_table DELETE TOPIC;
DROP STREAM IF EXISTS customer_stream;

CREATE STREAM customer_stream (
    rowkey       STRUCT<id BIGINT> key,
    username     VARCHAR(STRING),
    name         VARCHAR(STRING),
    email        VARCHAR(STRING),
    phone_number VARCHAR(STRING),
    __deleted    VARCHAR(STRING)
) WITH (KAFKA_TOPIC='customer', FORMAT='AVRO');

CREATE TABLE customer_table AS
SELECT
    rowkey -> id                                AS id,
    LATEST_BY_OFFSET(username, false)           AS username,
    LATEST_BY_OFFSET(name, false)               AS name,
    LATEST_BY_OFFSET(email, false)              AS email,
    LATEST_BY_OFFSET(phone_number, false)       AS phone_number,
    (LATEST_BY_OFFSET(__deleted, false) = 'true') AS is_deleted
FROM customer_stream
GROUP BY rowkey -> id;