package com.readonlydev.commands.staff.suggestions;

import java.awt.Color;
import java.util.Arrays;

import com.readonlydev.BotData;
import com.readonlydev.command.slash.SlashCommand;
import com.readonlydev.command.slash.SlashCommandEvent;
import com.readonlydev.database.entity.DBGalacticBot;
import com.readonlydev.util.Check;
import com.readonlydev.util.discord.Reply;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;

public class UpvoteRequirement extends SlashCommand
{

    public UpvoteRequirement()
    {
        this.name = "requirement";
        this.help = "Display or change the current upvotes required to be posted in Popular Suggestions";
        this.options = Arrays.asList(new OptionData(OptionType.INTEGER, "count", "Number of upvotes required").setRequiredRange(1, 100));
        this.subcommandGroup = new SubcommandGroupData("upvotes", "Suggestion Upvotes commands");
    }

    @Override
    protected void execute(SlashCommandEvent event)
    {
        if (event.getOptions().isEmpty())
        {
            boolean canRun = Check.staffRoles(event);

            if (!canRun)
            {
                Reply.InvalidPermissions(event);
                return;
            }

            final DBGalacticBot db = BotData.database().botDatabase();
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

            OptionMapping integerOption = event.getOptionsByType(OptionType.INTEGER).get(0);

            final DBGalacticBot db = BotData.database().botDatabase();
            int upvotesRequired = db.getSuggestionOptions().getStarRequirement();
            int newUpvotesRequired = integerOption.getAsInt();

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