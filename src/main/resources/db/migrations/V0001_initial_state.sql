create table users
(
    uuid           VARCHAR(255) PRIMARY KEY NOT NULL UNIQUE,
    username       VARCHAR(255)             NOT NULL UNIQUE,
    email          VARCHAR(255)             NOT NULL UNIQUE,
    firstName      VARCHAR(255),
    lastName       VARCHAR(255),
    password       VARCHAR(255)             NOT NULL,
    activated      BIT                      NOT NULL,
    reset_key      VARCHAR(255),
    activation_key VARCHAR(255),
    reset_date     DATETIME,
    created_date   DATETIME
);

create table user_authority
(
    id             INT PRIMARY KEY AUTO_INCREMENT,
    authority_name VARCHAR(255) NOT NULL,
    user_uuid      VARCHAR(255) NOT NULL,
    foreign key (user_uuid) references users (uuid)
);

create table authority
(
    id   INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL
);

insert into authority(name)
values ('ROLE_USER'),
       ('ROLE_ADMIN');

CREATE TABLE hotels
(
    uuid            VARCHAR(255) unique,
    external_id     INT          NOT NULL UNIQUE,
    name            VARCHAR(255) NOT NULL,
    provider        VARCHAR(255),
    favourite_count INT DEFAULT 0,
    photo_main_url  VARCHAR(255) NOT NULL,
    city            VARCHAR(255) NOT NULL
);

CREATE TABLE user_favourite_hotels
(
    user_uuid  VARCHAR(255) NOT NULL,
    hotel_uuid VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_uuid, hotel_uuid),
    FOREIGN KEY (user_uuid) REFERENCES users (uuid),
    FOREIGN KEY (hotel_uuid) REFERENCES hotels (uuid)
);