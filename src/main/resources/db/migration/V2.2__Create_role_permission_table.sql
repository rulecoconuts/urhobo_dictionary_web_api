create table role_permission(
    role_id int not null,
    permission_id int not null,
    primary key (role_id, permission_id),
    constraint fk_role_permission_role foreign key (role_id)
                            references role(id),
    constraint fk_role_permission_permission foreign key (permission_id)
                            references permission(id)
)