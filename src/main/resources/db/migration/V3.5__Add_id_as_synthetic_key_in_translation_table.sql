-- Drop primary key
alter table translation
    drop constraint translation_pkey;

-- Add new id bigserial column
alter table translation
    add column id bigserial primary key;

-- Add unique constraint to source_word_part and target_word_part
alter table translation
    add constraint uk_translation_source_word_part_id_target_word_part_id
        unique (source_word_part_id, target_word_part_id);