package io.github.romvoid95.commands.staff.suggestions;

import java.awt.Color;

import io.github.readonly.command.event.SlashCommandEvent;
import io.github.readonly.command.option.Option;
import io.github.romvoid95.BotData;
import io.github.romvoid95.commands.core.GalacticSlashCommand;
import io.github.romvoid95.database.entity.DBGalacticBot;
import io.github.romvoid95.util.Check;
import io.github.romvoid95.util.discord.Reply;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;

public class UpvoteRequirement extends GalacticSlashCommand
{

    public UpvoteRequirement()
    {
        this.name = "requirement";
        this.help = "Display or change the current upvotes required to be posted in Popular Suggestions";
        setOptions(
        	Option.integer("count", "Number of upvotes required")
        );
        this.subcommandGroup = new SubcommandGroupData("upvotes", "Suggestion Upvotes commands");
    }

    @Override
    protected void onExecute(SlashCommandEvent event)
    {
        if (event.getOptions().isEmpty())
        {
            boolean canRun = Check.staffRoles(event);

            if (!canRun)
            {
                Reply.InvalidPermissions(event);
                return;
            }

            final DBGalacticBot db = BotData.database().galacticBot();
            int upvotesRequired = db.getSuggestionOptions().getStarRequirement();

            //@noformat
            Reply.Success(event, new EmbedBuilder()
                .setColor(Color.ORANGE)
                .setTitle("Current Upvotes Required")
                .addField(EmbedBuilder.ZERO_WIDTH_SPACE, "**-->** `" + upvotesRequired + "` for suggestion to be considered popular", false)
            );
            //@format

        } else
        {
            boolean canRun = Check.adminRoles(event);

            if (!canRun)
            {
                Reply.InvalidPermissions(event);
                return;
            }

            final DBGalacticBot db = BotData.database().galacticBot();
            int upvotesRequired = db.getSuggestionOptions().getStarRequirement();
            int newUpvotesRequired = event.getOption("count").getAsInt();
            
            if(newUpvotesRequired < 1 || newUpvotesRequired > 100)
            {
                Reply.Error(event, "No operations performed: Number must be more than 0 and less than 101");
                return;
            }

            //Check if it is already the set channel
            if (upvotesRequired == newUpvotesRequired)
            {
                Reply.Success(event, "No operations performed: Selected Upvotes Required is already number required");
                return;
            }

            // Set new channel ID and send reply
            db.getSuggestionOptions().setStarRequirement(newUpvotesRequired);
            db.saveUpdating();
            Reply.Success(event, "Sucessfully changed Upvotes Required: `" + upvotesRequired + "` -> `" + newUpvotesRequired + "`");
        }
    }
}
