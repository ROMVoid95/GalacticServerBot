package io.github.romvoid95.commands.staff;

import io.github.romvoid95.commands.core.ParentSlashCommand;
import io.github.romvoid95.commands.staff.suggestions.ChannelLock;
import io.github.romvoid95.commands.staff.suggestions.ChannelUnlock;
import io.github.romvoid95.commands.staff.suggestions.StaffDeleteSuggestion;
import io.github.romvoid95.commands.staff.suggestions.PopularChannel;
import io.github.romvoid95.commands.staff.suggestions.PostChannel;
import io.github.romvoid95.commands.staff.suggestions.SuggestionBlacklist;
import io.github.romvoid95.commands.staff.suggestions.UpvoteRequirement;

public class Suggestions extends ParentSlashCommand
{

    public Suggestions()
    {
        super("suggestions");
        subCommands(
            new PostChannel(),
            new PopularChannel(),
            new ChannelLock(),
            new ChannelUnlock(),
            new UpvoteRequirement(),
            new StaffDeleteSuggestion(),
            new SuggestionBlacklist()
        );
    }
}
