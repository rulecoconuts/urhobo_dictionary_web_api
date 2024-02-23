alter table translation
    drop constraint translation_pkey;
alter table translation
    add constraint translation_pkey primary key (source_word_part_id, target_word_part_id);
alter table translation
    drop constraint fk_translation_dictionary;