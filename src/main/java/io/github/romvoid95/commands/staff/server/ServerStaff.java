package io.github.romvoid95.commands.staff.server;

import io.github.romvoid95.commands.core.ParentSlashCommand;

public class ServerStaff extends ParentSlashCommand
{

    public ServerStaff()
    {
        super("server");
        subCommands(
            new AddStaffRoles(),
            new RemoveStaffRole(),
            new ListStaffRoles()
        );
    }
}
