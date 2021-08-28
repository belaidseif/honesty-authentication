package com.honesty.authentication.user;


import java.util.Arrays;

public enum ApplicationUserPermission {
    POST_WRITE("post:write"),
    COMMENT_WRITE("comment:write"),
    MESSAGE_WRITE("message:write"),
    USER_EDIT("user:edit"),
    SYSTEM_CONFIG("system:config");

    private final String permission;
    ApplicationUserPermission(String permission){
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }

    public static ApplicationUserPermission getPermissionFromString(String permission){
        ApplicationUserPermission applicationUserPermission = null;
        for (ApplicationUserPermission value :
                ApplicationUserPermission.values()) {
            if(value.permission.equals(permission)) {
                applicationUserPermission = value;
                break;
            }
        }


        return applicationUserPermission;
    }
}
