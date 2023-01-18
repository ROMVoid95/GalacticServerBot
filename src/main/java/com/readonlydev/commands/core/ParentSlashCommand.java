package com.readonlydev.commands.core;

import com.readonlydev.command.slash.SlashCommand;
import com.readonlydev.command.slash.SlashCommandEvent;

public abstract class ParentSlashCommand extends SlashCommand
{
    
    public ParentSlashCommand()
    {
    }
    
    public ParentSlashCommand(String name)
    {
        this.name = name;
    }
    
    protected void subCommands(SlashCommand... children)
    {
        this.children = children;
    }

    @Override
    protected void execute(SlashCommandEvent event)
    {
        //NO-OP
    }
}
