package com.fejiro.exploration.dictionary.dictionary_web_api.security.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RolePermissionDataObjectId {
    Integer roleId;

    Integer permissionId;
}
