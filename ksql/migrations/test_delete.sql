CREATE STREAM user_detail_stream (
    rowkey       STRUCT<id BIGINT> key,
    username     VARCHAR(STRING),
    name         VARCHAR(STRING),
    email        VARCHAR(STRING),
    phone_number VARCHAR(STRING),
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
    LATEST_BY_OFFSET(__deleted, false)='true' as is_deleted
FROM user_detail_stream
GROUP BY rowkey->id;

CREATE STREAM user_detail_intermediate_stream (
    username     VARCHAR(STRING),
    name         VARCHAR(STRING),
    email        VARCHAR(STRING),
    phone_number VARCHAR(STRING),
    is_deleted   BOOLEAN
) WITH (KAFKA_TOPIC='USER_DETAIL_TABLE', FORMAT='AVRO');

CREATE TABLE user_details_pre_final AS
SELECT
    username,
    LATEST_BY_OFFSET(name, false) as name,
    LATEST_BY_OFFSET(email, false) as email,
    LATEST_BY_OFFSET(phone_number, false) as phone_number,
    LATEST_BY_OFFSET(is_deleted, false) as is_deleted
FROM user_detail_intermediate_stream
GROUP BY username;

CREATE STREAM user_detail_final_stream (
    username     VARCHAR(STRING) key,
    name         VARCHAR(STRING),
    email        VARCHAR(STRING),
    phone_number VARCHAR(STRING),
    is_deleted   BOOLEAN
) WITH (KAFKA_TOPIC='USER_DETAILS_PRE_FINAL', FORMAT='AVRO');

CREATE STREAM user_detail_final WITH (KAFKA_TOPIC='user_details_final', FORMAT='AVRO') AS
SELECT
    username,
    name,
    email,
    phone_number
FROM user_detail_final_stream
WHERE is_deleted=false;

CREATE STREAM user_detail_final_deleted WITH (KAFKA_TOPIC='user_details_final', KEY_FORMAT='AVRO', VALUE_FORMAT='KAFKA') AS
SELECT
    username,
    CAST(NULL AS VARCHAR)
FROM user_detail_final_stream
WHERE is_deleted=true;

drop stream user_detail_final_deleted;
drop stream user_detail_final delete topic;
drop stream user_detail_final_stream;
drop table user_details_pre_final delete topic;
drop stream user_detail_intermediate_stream;
drop table user_detail_table delete topic;
drop stream user_detail_stream;

