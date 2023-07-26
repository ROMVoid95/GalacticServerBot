package io.github.romvoid95.commands.staff.updates;

import io.github.romvoid95.commands.core.ParentSlashCommand;

public class UpdatesCommand extends ParentSlashCommand
{
	public UpdatesCommand()
	{
		super("updates");
		subCommands(
		    new ForceUpdates(),
			new AddCommand(),
			new RemoveCommand()
		);
	}
}
