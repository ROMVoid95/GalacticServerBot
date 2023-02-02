package io.github.romvoid95.commands.staff.updates;

import io.github.readonly.command.OptionHelper;
import io.github.readonly.command.event.SlashCommandEvent;
import io.github.readonly.command.option.Option;
import io.github.readonly.common.util.ResultLevel;
import io.github.romvoid95.BotData;
import io.github.romvoid95.commands.core.GalacticSlashCommand;
import io.github.romvoid95.database.entity.DBUpdates;
import io.github.romvoid95.database.impl.updates.Mod;
import io.github.romvoid95.util.discord.Reply;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class ModPlatformCommand extends GalacticSlashCommand
{
	public static final String NAME = "updates mod-platforms";
	
	public static final OptionData curseforgeOpt = Option.text("curseforge", "The CurseForge projectId (At least 1 platform must be provided)");
	public static final OptionData modrinthOpt = Option.text("modrinth", "The mods Modrinth projectId (At least 1 platform must be provided)");

	public ModPlatformCommand(OptionData... options)
	{
		this.name("mod-platforms");
		this.description("Set or update a mods Curseforge and/or Modrinth Data");
		this.setOptions(options);
	}
	
	@Override
	protected void onExecute(SlashCommandEvent event)
	{
		if(!OptionHelper.hasOption(event, "curseforge") && !OptionHelper.hasOption(event, "modrinth"))
		{
			Reply.EphemeralReply(event, ResultLevel.ERROR, "Neither a CurseForge or a Modrinth projectId was provided, At least 1 must be provided");
		}
		
		DBUpdates updates = BotData.database().updates();
		Mod mod = updates.getMod(OptionHelper.optString(event, "mod"));
		
		if(OptionHelper.hasOption(event, "curseforge"))
		{
			mod.getCurseforge().setProjectId(OptionHelper.optString(event, "curseforge"));
		}
		
		if(OptionHelper.hasOption(event, "modrinth"))
		{
			mod.getModrinth().setProjectId(OptionHelper.optString(event, "modrinth"));
		}
		
		updates.saveUpdating();
	}

}
