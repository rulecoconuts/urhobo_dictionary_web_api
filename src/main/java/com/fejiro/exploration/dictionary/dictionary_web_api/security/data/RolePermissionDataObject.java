package com.fejiro.exploration.dictionary.dictionary_web_api.security.data;


import com.fejiro.exploration.dictionary.dictionary_web_api.database.SimpleUserAndTemporalAuditable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class RolePermissionDataObject extends SimpleUserAndTemporalAuditable {

    Integer roleId;

    Integer permissionId;

    String restriction;

    public RolePermissionDataObjectId getId() {
        return new RolePermissionDataObjectId(roleId, permissionId);
    }
}
