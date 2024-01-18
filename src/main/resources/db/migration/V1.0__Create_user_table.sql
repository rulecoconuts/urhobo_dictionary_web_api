create table app_user
(
    id         serial primary key,
    username   varchar(255) not null unique,
    email      varchar(320) not null unique,
    first_name varchar(255),
    last_name  varchar(255),
    password   varchar(255) not null,
    created_at timestamp with time zone,
    updated_at timestamp with time zone
)