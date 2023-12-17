create table app_user
(
    id            serial        primary key,
    user_name     varchar(255) not null,
    email         varchar(320),
    first_name    varchar(255),
    last_name     varchar(255),
    password      varchar(255) not null,
    creation_date timestamp with time zone
)