create table word_part(
    id bigserial primary key,
    word_id bigint not null,
    part_id int not null,
    definition varchar(2500),
    note varchar(7500),
    created_by int,
    updated_by int,
    created_at timestamp with time zone,
    updated_at timestamp with time zone,
    constraint fk_language_created_by foreign key (created_by)
        references app_user(id),
    constraint fk_language_updated_by foreign key (updated_by)
        references app_user(id),
    constraint fk_word_part_word foreign key(word_id)
                      references word(id),
    constraint fk_word_part_part foreign key (part_id)
                      references part_of_speech(id),
    unique(word_id, part_id)
)