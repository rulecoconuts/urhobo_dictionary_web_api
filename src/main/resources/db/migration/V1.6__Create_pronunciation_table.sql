create table pronunciation(
    id bigserial primary key,
    phonetic_spelling varchar(9000),
    audio_url varchar(2048) not null,
    audio_byte_size bigint,
    audio_file_type varchar(200),
    audio_millisecond_duration int,
    word_part_id bigint not null,
    created_by int,
    updated_by int,
    created_at timestamp with time zone,
    updated_at timestamp with time zone,
    constraint fk_language_created_by foreign key (created_by)
        references app_user(id),
    constraint fk_language_updated_by foreign key (updated_by)
        references app_user(id),
    constraint fk_pronunciation_word_part foreign key(word_part_id)
                          references word_part(id)
)