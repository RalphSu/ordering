DROP TABLE IF EXISTS order;

CREATE TABLE order (
  ORDER_ID varchar(100) PRIMARY KEY NOT NULL,
  NAME varchar(100) NOT NULL,
  AGE smallint NOT NULL
);
