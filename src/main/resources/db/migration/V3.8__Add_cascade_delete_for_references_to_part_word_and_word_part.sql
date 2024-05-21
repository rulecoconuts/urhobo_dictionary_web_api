-- MAKE REFERENCES TO WORD PART CASCADE ON DELETE
-- Tables that reference word_part are translation and pronunciation
alter table public.translation
    drop constraint fk_translation_source_word,
    add constraint fk_translation_source_word foreign key (source_word_part_id)
        references word_part (id) on delete cascade,
    drop constraint fk_translation_target_word,
    add constraint fk_translation_target_word foreign key (target_word_part_id)
        references word_part (id) on delete cascade;

alter table public.pronunciation
    drop constraint fk_pronunciation_word_part,
    add constraint fk_pronunciation_word_part foreign key (word_part_id)
        references word_part (id) on delete cascade;

-- MAKE REFERENCES TO WORD and PART CASCADE ON DELETE
-- Tables that reference word are word_part
-- Tables that reference part are word_part
alter table public.word_part
    drop constraint fk_word_part_word,
    add constraint fk_word_part_word foreign key (word_id)
        references word (id) on delete cascade,
    drop constraint fk_word_part_part,
    add constraint fk_word_part_part foreign key (part_id)
        references part_of_speech (id) on delete cascade;