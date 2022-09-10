package com.readonlydev.commands.member;

import com.readonlydev.BotData;
import com.readonlydev.command.OptionHelper;
import com.readonlydev.command.slash.SlashCommand;
import com.readonlydev.command.slash.SlashCommandEvent;
import com.readonlydev.commands.core.SlashOptions;
import com.readonlydev.common.utils.ResultLevel;
import com.readonlydev.database.impl.Suggestion;
import com.readonlydev.util.discord.Emojis;
import com.readonlydev.util.discord.Reply;
import com.readonlydev.util.discord.SuggestionStatus;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class NewSuggestion extends SlashCommand
{

    public NewSuggestion()
    {
        this.name = "new-suggestion";
        this.options = SlashOptions.Suggestion.OptionsList();
        this.help = "Create a new Suggestion";
    }

    @Override
    protected void execute(SlashCommandEvent event)
    {
        String mention = event.getMember().getAsMention();

        String channelId = BotData.database().botDatabase().getSuggestionOptions().getSuggestionsChannelId();
        // Just in case some stupid stuff is going on
        if (channelId.isBlank())
        {
            Reply.EphemeralReply(event, ResultLevel.ERROR, "The Suggestions Channel for this server has not be set by the Staff");
            return;
        }

        TextChannel txtChannel = event.getGuild().getTextChannelById(channelId);
        // Ensure the channel is visable by the bot
        if (txtChannel == null)
        {
            Reply.EphemeralReply(event, ResultLevel.ERROR, "The Suggestions Channel that has been set for the server is invalid, Please inform Staff");
            return;
        }

        // Only allow new suggestions in the predefined channel
        // We don't live in the jungle out here
        if (!event.getChannel().asTextChannel().equals(txtChannel))
        {
            Reply.EphemeralReply(event, ResultLevel.ERROR, mention + "\n" + "Suggestion commands must be performed in " + txtChannel.getAsMention());
            return;
        }

        // We're not interested in suggestions for addon mods we can't control or have any say in
        if (event.getOption("type").getAsString().equals("existing"))
        {
            Reply.EphemeralReply(event, ResultLevel.ERROR, mention + "\n" + "Suggestions for existing addons are not being taken at this time, please use the addons discord for suggestions");
            return;
        }

        int number = BotData.database().botDatabase().getManager().getCount() + 1;
        String title = getTitle(event);

        //@noformat
        EmbedBuilder embed = new EmbedBuilder()
            .setColor(SuggestionStatus.NONE.getColor())
            .setTitle(getTitle(event))
            .setAuthor(event.getOption("type").getAsString())
            .setDescription("`Suggestion # %d`\n`By:` **%s**".formatted(number, mention))
            .addField("Description", OptionHelper.optString(event, "description"), false);
        //@format

        event.replyEmbeds(embed.build()).queue(s ->
        {
            s.retrieveOriginal().queue(reply ->
            {
                Suggestion newSuggestion = new Suggestion(reply.getId(), event.getMember().getId(), title);
                
                String newSuggestionId = BotData.database().botDatabase().addNewSuggestion(number, newSuggestion);
                reply.addReaction(Emojis.STAR.getEmoji()).queue();
                reply.createThreadChannel("Discussion").queue();
                
                event.getUser().openPrivateChannel().queue(c -> {
                    userChannelEmbed(c, title, newSuggestionId, event.getUser());
                });
            });
        });
    }

    private String getTitle(SlashCommandInteractionEvent event)
    {
        return OptionHelper.optString(event, "title");
    }
    
    private void userChannelEmbed(PrivateChannel channel, String title, String suggestionId, User user)
    {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Thanks for your suggestion " + user.getName());
        builder.setDescription(new StringBuilder()
        .append("This message contains the unique ID that can be used to edit the suggestion if you choose or find the need to do so later on. "
            + "Keep in mind that while you can edit the Title and Description of your suggestion, the Type of suggestion can not be changed. You can message an online "
            + "staff member and ask for the suggestion to be deleted if you have to change the suggestion type, or simply want it taken down.\n\n"
            + "**NOTE:** Edits are only available on Suggestions that have a status of Considered or No status. If you're requesting for the deletion to change the type, "
            + "and your suggestion is posted in the `popular-suggestions` channel."
            + " Your new updated suggestion will not take it's place. Staff cannot manually add suggestions to the `popular-suggestions` either.\n\n"
            + "Below you will find the unique ID that you need to use for any edits.").toString());
        builder.addField("**Unique ID**", suggestionId, false);
        builder.addField("Edit Commands", "There are two /slash commands you're able to use in this channel only. Typing '/' will show the available commands\n"
            + "Each command has all the required inputs needed and are self-explanatory.", false);

        channel.sendMessageEmbeds(builder.build()).queue();
    }
}
