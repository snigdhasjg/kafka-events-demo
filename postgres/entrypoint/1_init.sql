CREATE SCHEMA master;

CREATE OR REPLACE FUNCTION trigger_set_updated_at()
    RETURNS TRIGGER AS
$$
BEGIN
    NEW._updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE PLPGSQL;

create table user_detail
(
    id           bigserial
        constraint user_detail_pk primary key,
    username     varchar                 not null,
    name         varchar                 not null,
    email        varchar                 not null,
    phone_number varchar,
    _created_at  timestamp default now() not null,
    _updated_at  timestamp default now() not null
);

CREATE TRIGGER user_detail_trigger_set_updated_at
    BEFORE UPDATE
    ON user_detail
    FOR EACH ROW
EXECUTE PROCEDURE trigger_set_updated_at();

create unique index user_detail_username_uindex
    on user_detail (username);

create table address
(
    id          bigserial
        constraint address_pk primary key,
    user_id     bigint                  not null
        constraint address_user_detail__fk
            references user_detail (id)
            on delete cascade,
    type        varchar                 not null,
    field_1     varchar                 not null,
    field_2     varchar,
    state       varchar                 not null,
    city        varchar                 not null,
    country     varchar                 not null,
    pin_code    varchar                 not null,
    _created_at timestamp default now() not null,
    _updated_at timestamp default now() not null
);

CREATE TRIGGER address_set_updated_at
    BEFORE UPDATE
    ON address
    FOR EACH ROW
EXECUTE PROCEDURE trigger_set_updated_at();

create table user_click
(
    id          bigserial
        constraint user_click_pk
            primary key,
    user_id     bigint                  not null
        constraint user_click_user_detail_id_fk
            references user_detail (id)
            on delete cascade,
    type        varchar                 not null,
    is_error    boolean   default false,
    _created_at timestamp default now() not null
);

-- DATA

INSERT INTO user_detail(id, username, name, email, phone_number)
VALUES (1, 'iamsrk', 'Shah Rukh Khan', 'owner@redchillies.com', '+91-22-66699555'),
       (2, 'kamaalrkhan', 'Kamaal R. Khan', 'krk@therealkhan.com', '+91-22-66699556'),
       (3, 'duttsanjay', 'Sanjay Dutt', 'sanju@baba.com', '+91-22-66699556');

INSERT INTO address(user_id, type, field_1, field_2, state, city, country, pin_code)
VALUES (1, 'HOME', 'F1, SRK housing', 'Khan road', 'Maharastra', 'Mumbai', 'India', '200000'),
       (1, 'OFFICE', 'CEO office, Gouri flim', 'Flim city', 'Maharastra', 'Mumbai', 'India', '200001'),
       (3, 'HOME', '47, AK', 'Dutt lane', 'Maharastra', 'Mumbai', 'India', '200002');

INSERT INTO user_click(user_id, type, is_error)
VALUES (1, 'order', false),
       (1, 'add-to-cart', false),
       (1, 'add-to-cart', true),
       (1, 'payment', false),
       (2, 'browsing', false),
       (2, 'exit', false),
       (3, 'listing', true);