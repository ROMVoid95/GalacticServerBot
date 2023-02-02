package io.github.romvoid95.commands.core;

import io.github.readonly.common.util.KeyValueSupplier;

public enum RoleType implements KeyValueSupplier
{

    MOD("Moderator Roles"),
    ADMIN("Admin Roles");

    public final String name;

    RoleType(String name)
    {
        this.name = name;
    }

	@Override
	public String key()
	{
		return name;
	}

	@Override
	public String value()
	{
		return super.toString().toLowerCase();
	}
}