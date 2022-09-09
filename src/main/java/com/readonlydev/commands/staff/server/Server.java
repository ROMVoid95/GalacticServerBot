package com.readonlydev.commands.staff.server;

import com.readonlydev.commands.core.ParentSlashCommand;

public class Server extends ParentSlashCommand
{

    public Server()
    {
        super("server");
        subCommands(
            new AddStaffRole(),
            new RemoveStaffRole(),
            new ListStaffRoles()
        );
    }
}
