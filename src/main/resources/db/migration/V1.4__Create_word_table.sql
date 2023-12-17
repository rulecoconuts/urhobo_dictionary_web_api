create table word(
    id bigserial primary key,
    name varchar(3000) not null,
    language_id int not null,
    created_by int,
    updated_by int,
    created_at timestamp with time zone,
    updated_at timestamp with time zone,
    constraint fk_word_language foreign key (language_id)
        references language(id),
    constraint fk_language_created_by foreign key (created_by)
        references app_user(id),
    constraint fk_language_updated_by foreign key (updated_by)
        references app_user(id),
    unique (name, language_id)
)