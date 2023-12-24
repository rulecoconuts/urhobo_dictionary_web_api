create table user_role(
        user_id int not null,
        role_id int not null,
        primary key (user_id, role_id),
        constraint fk_user_role_user foreign key (user_id)
                  references app_user(id),
        constraint fk_user_role_role foreign key (role_id)
                references role(id)
)