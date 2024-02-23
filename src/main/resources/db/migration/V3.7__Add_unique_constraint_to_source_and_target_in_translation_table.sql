alter table translation
    add constraint uk_translation_source_word_part_id_target_word_part_id
        unique (source_word_part_id, target_word_part_id);