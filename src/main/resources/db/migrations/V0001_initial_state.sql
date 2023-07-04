create table users
(
    uuid       VARCHAR(255) PRIMARY KEY NOT NULL UNIQUE,
    username   VARCHAR(255)             NOT NULL UNIQUE,
    email      VARCHAR(255)             NOT NULL UNIQUE,
    firstName  VARCHAR(255),
    lastName   VARCHAR(255),
    password   VARCHAR(255)             NOT NULL,
    activated  BOOLEAN                  NOT NULL,
    reset_key  VARCHAR(255),
    activation_key VARCHAR(255),
    reset_date DATETIME,
    created_date DATETIME
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