package io.github.romvoid95.commands.core;

import io.github.readonly.command.SlashCommand;
import io.github.readonly.command.event.SlashCommandEvent;

public abstract class ParentSlashCommand extends SlashCommand
{
    public ParentSlashCommand(String name)
    {
        super(name);
    }
    
    protected void subCommands(SlashCommand... children)
    {
        this.subCommands = children;
    }

    @Override
    protected void execute(SlashCommandEvent event)
    {
        //NO-OP
    }
}
