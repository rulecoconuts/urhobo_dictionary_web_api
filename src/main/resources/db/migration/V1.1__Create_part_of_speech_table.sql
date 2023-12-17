create table part_of_speech(
    id serial primary key,
    name varchar(255) not null,
    description varchar(2500),
    created_by int,
    updated_by int,
    created_at timestamp with time zone,
    updated_at timestamp with time zone,
    constraint fk_part_of_speech_created_by foreign key (created_by)
                           references app_user(id),
    constraint fk_part_of_speech_updated_by foreign key (updated_by)
                           references app_user(id)
)