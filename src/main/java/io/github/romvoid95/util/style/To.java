package io.github.romvoid95.util.style;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class To
{
    public static String String(final Object object) {
        return new ReflectionToStringBuilder(object, Style.JsonStyle).build();
    }
}
