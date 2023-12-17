create table translation(
    dictionary_id int not null,
    source_word_part_id bigint not null,
    target_word_part_id bigint not null,
    note varchar(7500),
    reverse_note varchar(7500),
    created_by int,
    updated_by int,
    created_at timestamp with time zone,
    updated_at timestamp with time zone,
    constraint fk_language_created_by foreign key (created_by)
        references app_user(id),
    constraint fk_language_updated_by foreign key (updated_by)
        references app_user(id),
    constraint fk_translation_dictionary foreign key (dictionary_id)
                        references dictionary(id),
    constraint fk_translation_source_word foreign key (source_word_part_id)
                        references word_part(id),
    constraint fk_translation_target_word foreign key (target_word_part_id)
                        references word_part(id),
    primary key (dictionary_id, source_word_part_id, target_word_part_id)
)