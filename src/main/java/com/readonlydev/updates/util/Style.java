package com.readonlydev.updates.util;

import lombok.Getter;

@Getter
public enum Style
{

    Format("format"), PlainText("text"), DiscordMarkdown("discordmd"), None(null);

    private final String type;

    Style(final String type)
    {
        this.type = type;
    }

    public static Style getStyle(final String template)
    {
        for (Style style : Style.values())
        {
            if (style != null && template.startsWith(style.type))
            {
                return style;
            }
        }

        return Style.None;
    }
}
