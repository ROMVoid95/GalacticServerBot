package io.github.romvoid95.commands.member;

import java.util.UUID;

import io.github.readonly.command.OptionHelper;
import io.github.readonly.command.event.SlashCommandEvent;
import io.github.readonly.command.lists.ChoiceList;
import io.github.readonly.command.option.Choice;
import io.github.readonly.command.option.RequiredOption;
import io.github.readonly.common.util.ResultLevel;
import io.github.romvoid95.BotData;
import io.github.romvoid95.Conf;
import io.github.romvoid95.Servers;
import io.github.romvoid95.commands.core.GalacticSlashCommand;
import io.github.romvoid95.database.impl.Suggestion;
import io.github.romvoid95.server.Server;
import io.github.romvoid95.util.discord.Emojis;
import io.github.romvoid95.util.discord.Reply;
import io.github.romvoid95.util.discord.SuggestionsHelper;
import io.github.romvoid95.util.discord.entity.SuggestionMessage;
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
        name("new-suggestion");
        description("Create a new Suggestion");
        setOptions(RequiredOption.text("type", "Suggestion Type", ChoiceList.of(Choice.add("Galacticraft 5", "[Galacticraft 5]"), Choice.add("Galacticraft-Legacy", "[Galacticraft-Legacy]"), Choice.add("Galacticraft [Both Versions]", "[Galacticraft - Both Versions]"),
            Choice.add("Idea For New Addon", "[Addon Idea]"), Choice.add("Do Not Make Suggestions For Existing Addons", "existing"))), RequiredOption.text("title", "Short generalized title for your suggestion", 64), RequiredOption.text("description", "Describe in detail your suggestion"));
    }

    @Override
    protected void onExecute(SlashCommandEvent event)
    {
        String mention = event.getMember().getUser().getGlobalName();

        boolean isBlacklisted = BotData.database().blacklist().isBlacklisted(event.getMember().getId());
        if (isBlacklisted)
        {
            Reply.EphemeralReply(event, ResultLevel.ERROR, "You have been blacklisted and cannot make new suggestions, Contact staff if you believe this is an error");
            return;
        }

        if (!Server.of(event.getGuild()).equals(Servers.galacticraftCentral))
        {
            Reply.EphemeralReply(event, ResultLevel.ERROR, "This command can only be used in the Galacticraft Central Discord Server");
            return;
        }

        String      channelId  = BotData.database().galacticBot().getSuggestionOptions().getSuggestionsChannelId();
        TextChannel txtChannel = event.getGuild().getTextChannelById(channelId);
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

        int    number                = BotData.database().galacticBot().getManager().getCount() + 1;
        String type                  = event.getOption("type").getAsString();
        String title                 = getTitle(event);
        String suggestionDescription = event.getOption("description").getAsString();
        String numberAndAuthor       = "`Suggestion # %d`\n`By:` **%s**".formatted(number, mention);

        if (Conf.Bot().isOwner(event.getUser()) && (title.equalsIgnoreCase("test") || suggestionDescription.equalsIgnoreCase("test")))
        {
            String[] uuid = UUID.randomUUID().toString().split("-");
            sendPrivateMessage(event.getUser().openPrivateChannel(), event.getUser(), number, title, uuid[uuid.length - 1]);
            Reply.Success(event, "Test Message Sent");
            return;
        }

        event.replyEmbeds(SuggestionMessage.builder().title(title).type(type).numberAndAuthor(numberAndAuthor).description(SuggestionMessage.split.apply(suggestionDescription)).build()).queue(s ->
        {
            s.retrieveOriginal().queue(reply ->
            {
                Suggestion newSuggestion = new Suggestion(reply.getId(), event.getMember().getId(), title);

                String newSuggestionId = BotData.database().galacticBot().addNewSuggestion(number, newSuggestion);
                reply.addReaction(Emojis.STAR.getEmoji()).queue();
                reply.createThreadChannel(title).queue();

                sendPrivateMessage(event.getUser().openPrivateChannel(), event.getUser(), number, title, newSuggestionId);
            });
        });
    }

    private void sendPrivateMessage(CacheRestAction<PrivateChannel> action, User user, int number, String title, String newSuggestionId)
    {
        action.flatMap(c -> c.sendMessage(new MessageCreateBuilder().setEmbeds(userChannelEmbed(number, title, newSuggestionId, user)).build())).queue();
    }

    private String getTitle(SlashCommandInteractionEvent event)
    {
        return OptionHelper.optString(event, "title");
    }

    private MessageEmbed userChannelEmbed(int number, String title, String suggestionId, User user)
    {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Thanks for your suggestion " + user.getName());
        if (!SuggestionsHelper.getAllAuthors().contains(user.getId()))
        {
            builder.setDescription(getFirstTimeSuggestionMessage());
        }
        builder.addField("Title", "`" + title + "`", false);
        builder.addField("Suggestion", "`#" + number + "`", true);
        builder.addField("**Unique ID**", "`" + suggestionId + "`", true);
        builder.addField("Edit Command",
            //@noformat
            "`/edit <id> <section> <edit-type> <content>`\n" + 
                "**id**: The Unique ID provided to you by the Bot\n" + 
                "**section**: Choose either the Title or Description to edit\n" + 
                "**edit-type**: *Replace*, *Append*, or *Prepend* current value\n" +
                "**content**: The content to perform the edit-type with on your suggestion",
            false);
        builder.addField("Delete Command", 
            "`/delete <id>`\n" + 
                "**id**: The Unique ID provided to you by the Bot\n", false);
        return builder.build();
    }

    private String getFirstTimeSuggestionMessage()
    {
        return "This message contains the unique ID that can be used to edit the suggestion if you choose or find the need to do so later on. " + "Keep in mind that while you can edit the Title and Description of your suggestion, the Type of suggestion can not be changed.\n\n"
            + "**NOTE:** Edits are only available on Suggestions that have a status of Considered or No status. Staff cannot manually add suggestions to the `popular-suggestions` either.\n\n" + "Below you will find the unique ID that you need to use for any edits.";
    }
}
