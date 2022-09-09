package com.readonlydev.commands.staff;

import com.readonlydev.commands.core.ParentSlashCommand;
import com.readonlydev.commands.staff.suggestions.ChannelLock;
import com.readonlydev.commands.staff.suggestions.ChannelUnlock;
import com.readonlydev.commands.staff.suggestions.DeleteSuggestion;
import com.readonlydev.commands.staff.suggestions.PopularChannel;
import com.readonlydev.commands.staff.suggestions.PostChannel;
import com.readonlydev.commands.staff.suggestions.UpvoteRequirement;

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
            new DeleteSuggestion()
        );
    }
}
