CREATE TABLE customer (
     id int,
     username varchar(255),
     name varchar(255),
     email varchar(255),
     phone_number varchar(255),
     PRIMARY KEY (id)
);

insert into customer
values (1, 'imsrk', 'SRK', 'red@chillis.com', '+91222222'),
       (2, 'abcd', 'SRK', 'red@chillis.com', '+91222222'),
       (3, 'casljbcajs', 'SRK', 'red@chillis.com', '+91222222'),
       (4, '1234', 'SRK', 'red@chillis.com', '+91222222');

DELETE from customer where id=3;

insert into customer
values (5, 'ayush', 'Abcd', 'red@chillis.com', '+91222222');