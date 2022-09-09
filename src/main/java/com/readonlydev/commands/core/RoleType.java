package com.readonlydev.commands.core;

public enum RoleType
{

    MOD("Moderator Roles"),
    ADMIN("Admin Roles");

    public final String name;

    RoleType(String name)
    {
        this.name = name;
    }
}