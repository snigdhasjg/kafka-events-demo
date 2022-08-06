drop table if exists user_details_final delete topic;
drop stream if exists user_detail_intermediate_stream;
drop table if exists user_detail_table delete topic;
drop stream if exists user_detail_stream;

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

CREATE TABLE user_details_final WITH (KAFKA_TOPIC='user_details_final', FORMAT='AVRO') AS
SELECT
    username,
    LATEST_BY_OFFSET(name, false) as name,
    LATEST_BY_OFFSET(email, false) as email,
    LATEST_BY_OFFSET(phone_number, false) as phone_number
FROM user_detail_intermediate_stream
GROUP BY username
HAVING LATEST_BY_OFFSET(is_deleted, false)=false;
