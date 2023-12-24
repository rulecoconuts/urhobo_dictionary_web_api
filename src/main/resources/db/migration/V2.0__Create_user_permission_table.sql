create table user_permission(
    user_id int not null,
    permission_id int not null,
    primary key (user_id, permission_id),
    constraint fk_user_permission_user foreign key (user_id)
                            references app_user(id),
    constraint fk_user_permission_permission foreign key (permission_id)
                            references permission(id)
)