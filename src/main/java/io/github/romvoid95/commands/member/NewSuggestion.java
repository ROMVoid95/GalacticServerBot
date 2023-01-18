package io.github.romvoid95.commands.member;

import java.util.UUID;

import com.github.readonlydevelopment.command.OptionHelper;
import com.github.readonlydevelopment.command.event.SlashCommandEvent;
import com.github.readonlydevelopment.common.utils.ResultLevel;

import io.github.romvoid95.BotData;
import io.github.romvoid95.Conf;
import io.github.romvoid95.Server;
import io.github.romvoid95.commands.core.GalacticSlashCommand;
import io.github.romvoid95.commands.core.SlashOptions;
import io.github.romvoid95.database.impl.Suggestion;
import io.github.romvoid95.util.discord.Emojis;
import io.github.romvoid95.util.discord.Reply;
import io.github.romvoid95.util.discord.SuggestionStatus;
import io.github.romvoid95.util.discord.SuggestionsHelper;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.requests.restaction.CacheRestAction;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;

public class NewSuggestion extends GalacticSlashCommand
{

    public NewSuggestion()
    {
        this.name = "new-suggestion";
        this.options = SlashOptions.Suggestion.OptionsList();
        this.help = "Create a new Suggestion";
    }

    @Override
    protected void onExecute(SlashCommandEvent event)
    {
        String mention = event.getMember().getAsMention();

        boolean isBlacklisted = BotData.database().blacklist().isBlacklisted(event.getMember().getId());
        if(isBlacklisted)
        {
            Reply.EphemeralReply(event, ResultLevel.ERROR, "You have been blacklisted and cannot make new suggestions, Contact staff if you believe this is an error");
            return;
        }
        
        if(!Server.getServer(event.getGuild()).equals(BotData.galacticraftCentralServer()))
        {
        	Reply.EphemeralReply(event, ResultLevel.ERROR, "This command can only be used in the Galacticraft Central Discord Server");
        }
        
        String channelId = BotData.database().botDatabase().getSuggestionOptions().getSuggestionChannel();
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
        if(!event.getChannel().asTextChannel().equals(txtChannel))
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
        String description = event.getOption("description").getAsString();
        
        //@noformat
        EmbedBuilder embed = new EmbedBuilder()
            .setColor(SuggestionStatus.NONE.getColor())
            .setTitle(getTitle(event))
            .setAuthor(event.getOption("type").getAsString())
            .setDescription("`Suggestion # %d`\n`By:` **%s**".formatted(number, mention))
            .addField("Description", description, false);
        //@format
        
        if(Conf.Bot().isOwner(event.getUser()) && (title.equalsIgnoreCase("test") || description.equalsIgnoreCase("test"))) {
            String[] uuid = UUID.randomUUID().toString().split("-");
            sendPrivateMessage(event.getUser().openPrivateChannel(), event.getUser(), number, title, uuid[uuid.length - 1]);
            Reply.Success(event, "Test Message Sent");
            return;
        }

        event.replyEmbeds(embed.build()).queue(s ->
        {
            s.retrieveOriginal().queue(reply ->
            {
                Suggestion newSuggestion = new Suggestion(reply.getId(), event.getMember().getId(), title);
                
                String newSuggestionId = BotData.database().botDatabase().addNewSuggestion(number, newSuggestion);
                reply.addReaction(Emojis.STAR.getEmoji()).queue();
                reply.createThreadChannel("Discussion").queue();
                
                sendPrivateMessage(event.getUser().openPrivateChannel(), event.getUser(), number, title, newSuggestionId);
            });
        });
    }
    
    private void sendPrivateMessage(CacheRestAction<PrivateChannel> action, User user, int number, String title, String newSuggestionId)
    {
        action.flatMap(c -> c.sendMessage(new MessageCreateBuilder().setEmbeds(userChannelEmbed(number, title, newSuggestionId, user)).build()))
        .queue();
    }

    private String getTitle(SlashCommandInteractionEvent event)
    {
        return OptionHelper.optString(event, "title");
    }
    
    private MessageEmbed userChannelEmbed(int number, String title, String suggestionId, User user)
    {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Thanks for your suggestion " + user.getName());
        if(!SuggestionsHelper.getAllAuthors().contains(user.getId()))
        {
            builder.setDescription(getFirstTimeSuggestionMessage());
        }
        builder.addField("Title", "`" + title + "`", false);
        builder.addField("Suggestion", "`#" + number + "`", true);
        builder.addField("**Unique ID**", "`" + suggestionId + "`", true);
        builder.addField("Edit Command", 
            "`/edit <section> <type> <content>`\n" + 
            "**section**: Choice between editing the Title or Description\n" +
            "**type**: Choice between Replacing or to Append to the end of what your editing\n" + 
            "**content**: The content which will be replacing or appended to the section your editing", false);
        return builder.build();
    }
    
    private String getFirstTimeSuggestionMessage()
    {
        return "This message contains the unique ID that can be used to edit the suggestion if you choose or find the need to do so later on. "
            + "Keep in mind that while you can edit the Title and Description of your suggestion, the Type of suggestion can not be changed. You can message an online "
            + "staff member and ask for the suggestion to be deleted if you have to change the suggestion type, or simply want it taken down.\n\n"
            + "**NOTE:** Edits are only available on Suggestions that have a status of Considered or No status. If you're requesting for the deletion to change the type, "
            + "and your suggestion is posted in the `popular-suggestions` channel."
            + " Your new updated suggestion will not take it's place. Staff cannot manually add suggestions to the `popular-suggestions` either.\n\n"
            + "Below you will find the unique ID that you need to use for any edits.";
    }
}
