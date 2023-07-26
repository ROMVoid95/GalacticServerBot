package io.github.romvoid95.commands.staff.updates;

import io.github.readonly.command.event.SlashCommandEvent;
import io.github.readonly.common.util.ResultLevel;
import io.github.romvoid95.commands.core.GalacticSlashCommand;
import io.github.romvoid95.updates.UpdateManager;
import io.github.romvoid95.util.Check;
import io.github.romvoid95.util.Embed;
import io.github.romvoid95.util.discord.Reply;

public class ForceUpdates extends GalacticSlashCommand
{
    public ForceUpdates()
    {
        this.name("force-updates");
        this.description("Forces a check on mod updates");
    }
    @Override
    protected void onExecute(SlashCommandEvent event)
    {
        if (Check.userNotStaff(event))
        {
            Reply.InvalidPermissions(event);
            return;
        }
        event.deferReply().setEphemeral(true).queue();
        
        UpdateManager.instance().getApiManager().get().allmods().run();
        Reply.EphemeralReply(event.getHook(), ResultLevel.SUCCESS, Embed.newBuilder().description("Running update checks").toEmbed());
    }
}
