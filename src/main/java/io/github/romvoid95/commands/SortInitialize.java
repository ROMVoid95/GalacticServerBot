package io.github.romvoid95.commands;

import java.util.Arrays;
import java.util.List;

import io.github.readonly.command.ClientBuilder;
import io.github.readonly.command.SlashCommand;
import io.github.romvoid95.Servers;
import io.github.romvoid95.commands.member.CloseDiscussionThread;
import io.github.romvoid95.commands.member.NewSuggestion;
import io.github.romvoid95.commands.owner.AddEmojiCommand;
import io.github.romvoid95.commands.owner.DeleteFromListCommand;
import io.github.romvoid95.commands.owner.EvalCommand;
import io.github.romvoid95.commands.owner.ExecCommand;
import io.github.romvoid95.commands.owner.MaintanenceModeCommand;
import io.github.romvoid95.commands.owner.SetCountCommand;
import io.github.romvoid95.commands.owner.StatusOverrideCommand;
import io.github.romvoid95.commands.staff.Suggestions;
import io.github.romvoid95.commands.staff.server.ServerStaff;
import io.github.romvoid95.commands.staff.suggestions.devonly.DevServerPopularChannel;
import io.github.romvoid95.commands.staff.suggestions.devonly.SuggestionSetStatus;
import io.github.romvoid95.commands.staff.updates.UpdatesCommand;

public class SortInitialize
{

    public static void perform(ClientBuilder clientBuilder)
    {
        Servers.readOnlyNetwork.addSlashCommands(SortInitialize.botServerCommands());
        Servers.galacticraftCentral.addSlashCommands(SortInitialize.centralServerCommands());
        Servers.teamGalacticraft.addSlashCommands(SortInitialize.teamServerCommands());

        clientBuilder.addServerCommands(Servers.readOnlyNetwork.getServerCommands());
        clientBuilder.addServerCommands(Servers.galacticraftCentral.getServerCommands());
        clientBuilder.addServerCommands(Servers.teamGalacticraft.getServerCommands());
    }

    //@noformat
    private static final List<SlashCommand> centralServerCommands()
    {
        return Arrays.asList(
            new Suggestions(),
            new ServerStaff(),
            new NewSuggestion(),
            new CloseDiscussionThread(), 
           new UpdatesCommand()
        );
    }

    private static final List<SlashCommand> teamServerCommands()
    {
        return Arrays.asList(
            new DevServerPopularChannel(),
            new SuggestionSetStatus(),
            new UpdatesCommand()
        );
    }

    private static final List<SlashCommand> botServerCommands()
    {
        return Arrays.asList(
            new DeleteFromListCommand(),
            new EvalCommand(),
            new ExecCommand(),
            new MaintanenceModeCommand(),
            new UpdatesCommand(),
            new SetCountCommand(),
            new StatusOverrideCommand(),
            new AddEmojiCommand()
        );
    }
}
