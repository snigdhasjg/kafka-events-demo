CREATE SCHEMA master;
set search_path=master;

CREATE OR REPLACE FUNCTION trigger_set_updated_at()
    RETURNS TRIGGER AS
$$
BEGIN
    NEW._updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE PLPGSQL;

create table country
(
    iso_code    varchar(10) primary key,
    name        varchar                 not null,
    _created_at timestamp default now() not null,
    _updated_at timestamp default now() not null,
    unique (name)
);

CREATE TRIGGER country_trigger_set_updated_at
    BEFORE UPDATE
    ON country
    FOR EACH ROW
EXECUTE PROCEDURE trigger_set_updated_at();

create table user_detail
(
    id           bigserial primary key,
    username     varchar                 not null,
    name         varchar                 not null,
    email        varchar,
    phone_number varchar,
    country_iso  varchar
        constraint user_detail_country__fk references country (iso_code),
    _created_at  timestamp default now() not null,
    _updated_at  timestamp default now() not null,
    unique (username)
);

CREATE TRIGGER user_detail_trigger_set_updated_at
    BEFORE UPDATE
    ON user_detail
    FOR EACH ROW
EXECUTE PROCEDURE trigger_set_updated_at();

-- DATA

INSERT INTO country (iso_code, name)
values ('IN', 'India'),
       ('US', 'USA'),
       ('CN', 'China');

INSERT INTO user_detail(id, username, name, country_iso)
VALUES (1, 'iamsrk', 'Shah Rukh Khan', 'IN'),
       (2, 'kamaalrkhan', 'Kamaal R. Khan', 'IN'),
       (3, 'duttsanjay', 'Sanjay Dutt', 'US');

UPDATE user_detail set country_iso='CN' where id=1;
