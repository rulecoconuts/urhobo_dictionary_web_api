create table refresh_token
(
    id          bigserial primary key,
    user_id     int     not null,
    content     varchar not null,
    enabled     boolean not null,
    expiry_date timestamp with time zone,
    created_at  timestamp with time zone,
    updated_at  timestamp with time zone,
    constraint fk_refresh_token_user_id foreign key (user_id)
        references app_user (id)
)