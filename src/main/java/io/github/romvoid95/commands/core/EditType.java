package io.github.romvoid95.commands.core;

import io.github.readonly.common.util.KeyValueSupplier;
import io.github.romvoid95.util.StringUtils;

public enum EditType implements KeyValueSupplier
{
    REPLACE,
    PREPEND,
    APPEND;

    public static EditType getEditType(String value)
    {
        return EditType.valueOf(value);
    }

	@Override
	public String key()
	{
		return StringUtils.capitalize(toString().toLowerCase());
	}

	@Override
	public String value()
	{
		return toString();
	}
}
