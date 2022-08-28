-- https://rmoff.net/2020/11/03/kafka-connect-ksqldb-and-kafka-tombstone-messages/

drop stream if exists user_detail_final_deleted;
drop stream if exists user_detail_final delete topic;
drop stream if exists user_detail_final_stream;
drop table if exists user_details_pre_final delete topic;
drop stream if exists user_detail_intermediate_stream;
drop table if exists user_detail_table delete topic;
drop stream if exists user_detail_stream;

CREATE STREAM user_detail_stream (
    rowkey       STRUCT<id BIGINT> key,
    username     VARCHAR(STRING),
    name         VARCHAR(STRING),
    email        VARCHAR(STRING),
    phone_number VARCHAR(STRING),
    country_iso  VARCHAR(STRING),
    _created_at  BIGINT,
    _updated_at  BIGINT,
    __deleted    VARCHAR(STRING)
) WITH (KAFKA_TOPIC='user_detail', FORMAT='AVRO');

CREATE TABLE user_detail_table AS
SELECT
    rowkey->id as id,
    LATEST_BY_OFFSET(
        case
            when username='' then null
            else username
        end
    ) as username,
    LATEST_BY_OFFSET(name, false) as name,
    LATEST_BY_OFFSET(email, false) as email,
    LATEST_BY_OFFSET(phone_number, false) as phone_number,
    LATEST_BY_OFFSET(country_iso, false) as country_iso,
    LATEST_BY_OFFSET(__deleted, false)='true' as is_deleted
FROM user_detail_stream
GROUP BY rowkey->id;

CREATE STREAM country_stream (
    rowkey      STRUCT<iso_code VARCHAR(STRING)> key,
    name        VARCHAR(STRING),
    _created_at BIGINT,
    _updated_at BIGINT,
    __deleted   VARCHAR(STRING)
) WITH (KAFKA_TOPIC='country', FORMAT='AVRO');

CREATE TABLE country_table AS
SELECT
    rowkey -> iso_code as iso_code,
    LATEST_BY_OFFSET(
        case
            when name='' then null
            else name
        end
    ) as name,
    LATEST_BY_OFFSET(__deleted, false)='true' as is_deleted
FROM country_stream
GROUP BY rowkey -> iso_code;

CREATE TABLE user_detail_with_country_table AS
SELECT udt.id,
       udt.username,
       udt.name,
       udt.email,
       udt.phone_number,
       ct.name as country_name,
       udt.is_deleted
FROM user_detail_table udt
         LEFT JOIN country_table ct on udt.country_iso = ct.iso_code and ct.is_deleted=false;

CREATE STREAM user_detail_intermediate_stream (
    username     VARCHAR(STRING),
    name         VARCHAR(STRING),
    email        VARCHAR(STRING),
    phone_number VARCHAR(STRING),
    country_name VARCHAR(STRING),
    is_deleted   BOOLEAN
) WITH (KAFKA_TOPIC='USER_DETAIL_TABLE', FORMAT='AVRO');

CREATE TABLE user_details_pre_final AS
SELECT
    username,
    LATEST_BY_OFFSET(name, false) as name,
    LATEST_BY_OFFSET(email, false) as email,
    LATEST_BY_OFFSET(phone_number, false) as phone_number,
    LATEST_BY_OFFSET(country_name, false) as country_name,
    LATEST_BY_OFFSET(is_deleted, false) as is_deleted
FROM user_detail_intermediate_stream
GROUP BY username;

CREATE STREAM user_detail_final_stream (
    username     VARCHAR(STRING) key,
    name         VARCHAR(STRING),
    email        VARCHAR(STRING),
    phone_number VARCHAR(STRING),
    country_name VARCHAR(STRING),
    is_deleted   BOOLEAN
) WITH (KAFKA_TOPIC='USER_DETAILS_PRE_FINAL', FORMAT='AVRO');

CREATE STREAM user_detail_final WITH (KAFKA_TOPIC='user_details_final', FORMAT='AVRO') AS
SELECT
    username,
    name,
    email,
    phone_number,
    country_name
FROM user_detail_final_stream
WHERE is_deleted=false;

CREATE STREAM user_detail_final_deleted WITH (KAFKA_TOPIC='user_details_final', KEY_FORMAT='AVRO', VALUE_FORMAT='KAFKA') AS
SELECT
    username,
    CAST(NULL AS VARCHAR)
FROM user_detail_final_stream
WHERE is_deleted=true;

