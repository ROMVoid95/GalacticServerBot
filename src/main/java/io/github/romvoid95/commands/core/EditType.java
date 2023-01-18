package io.github.romvoid95.commands.core;

import java.util.ArrayList;
import java.util.List;

import io.github.romvoid95.util.StringUtils;
import net.dv8tion.jda.api.interactions.commands.Command;

public enum EditType
{
    REPLACE,
    APPEND;
    
    public static List<Command.Choice> toChoices()
    {
        List<Command.Choice> choices = new ArrayList<>();
        for(EditType type : EditType.values())
        {
            String name = StringUtils.capitalize(type.toString().toLowerCase());
            choices.add(new Command.Choice(name, type.toString()));
        }
        return choices;
    }
    
    public static EditType getEditType(String value)
    {
        return EditType.valueOf(value);
    }
}
