package com.readonlydev.util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class RuntimeOptions {
	public static final boolean DEBUG = getValue("DEBUG") != null;
	public static final boolean DEBUG_LOGS = getValue("DEBUG_LOGS") != null;
	public static final boolean LOG_DB_ACCESS = getValue("LOG_DB_ACCESS") != null;
	public static final boolean TRACE_LOGS = getValue("TRACE_LOGS") != null;
	public static final boolean VERBOSE = getValue("VERBOSE") != null;
	public static final boolean PRINT_VARIABLES = getValue("PRINT_OPTIONS") != null;

	@Nullable
	private static String getValue(@Nonnull String name) {
		return System.getProperty(name, System.getenv(name));
	}
}
