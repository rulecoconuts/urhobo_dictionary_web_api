create table dictionary(
    id serial primary key,
    source_language_id int not null,
    target_language_id int not null,
    created_by int,
    updated_by int,
    created_at timestamp with time zone,
    updated_at timestamp with time zone,
    constraint fk_dictionary_language_source foreign key (source_language_id)
        references language(id),
    constraint fk_dictionary_language_target foreign key (target_language_id)
        references  language(id),
    constraint fk_part_of_speech_created_by foreign key (created_by)
        references app_user(id),
    constraint fk_part_of_speech_updated_by foreign key (updated_by)
        references app_user(id)
)