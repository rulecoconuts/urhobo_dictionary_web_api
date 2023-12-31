create table language (
    id serial primary key,
    name varchar(255) not null,
    description varchar(10000),
    created_by int,
    updated_by int,
    created_at timestamp with time zone,
    updated_at timestamp with time zone,
    constraint fk_language_created_by foreign key (created_by)
        references app_user(id),
    constraint fk_language_updated_by foreign key (updated_by)
        references app_user(id)
)