package com.readonlydev.commands.staff.server;

import com.readonlydev.commands.core.ParentSlashCommand;

public class ServerStaff extends ParentSlashCommand
{

    public ServerStaff()
    {
        super("server");
        subCommands(
            new AddStaffRole(),
            new RemoveStaffRole(),
            new ListStaffRoles()
        );
    }
}
