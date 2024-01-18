alter table role_permission
    add column created_by int
        constraint fk_role_created_by references app_user (id),
    add column updated_by int
        constraint fk_role_updated_by references app_user (id),
    add column created_at timestamp with time zone,
    add column updated_at timestamp with time zone;