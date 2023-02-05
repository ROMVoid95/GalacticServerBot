package io.github.romvoid95.commands.owner;

import io.github.readonly.command.OptionHelper;
import io.github.readonly.command.event.SlashCommandEvent;
import io.github.readonly.command.option.RequiredOption;
import io.github.romvoid95.BotData;
import io.github.romvoid95.commands.core.GalacticSlashCommand;
import io.github.romvoid95.database.entity.DBUpdates;
import io.github.romvoid95.database.impl.updates.UpdateMod;
import io.github.romvoid95.database.impl.updates.UpdateMod.Curseforge;
import io.github.romvoid95.util.Check;
import io.github.romvoid95.util.discord.Reply;

public class TestCommand2 extends GalacticSlashCommand
{

    public TestCommand2()
    {
        name("testing-2");
        setOptions(
        	RequiredOption.text("mod", "The mod"),
        	RequiredOption.integer("fileid", "FileID to set")
        );
    }

    @Override
    protected void execute(SlashCommandEvent event)
    {
        if(!Check.isOwner(event))
        {
            Reply.InvalidPermissions(event);
            return;
        }
        super.execute(event);
    }

    @Override
    protected void onExecute(SlashCommandEvent event)
    {
    	DBUpdates	updates	= BotData.database().updates();
    	UpdateMod mod = updates.getMod(OptionHelper.optString(event, "mod"));
    	
    	Curseforge curse = new Curseforge(mod.getCurseforge().getProjectId(), OptionHelper.optInteger(event, "fileid"));
    	mod.setCurseforge(curse);
    	updates.saveUpdating();
    	event.reply("Updated to fileId: " + OptionHelper.optInteger(event, "fileid")).setEphemeral(true).queue();
    }
}
