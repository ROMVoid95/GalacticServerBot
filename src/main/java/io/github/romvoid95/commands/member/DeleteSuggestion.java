package io.github.romvoid95.commands.member;

import java.util.concurrent.TimeUnit;

import io.github.readonly.command.event.SlashCommandEvent;
import io.github.readonly.command.option.RequiredOption;
import io.github.readonly.common.util.RGB;
import io.github.readonly.common.util.ResultLevel;
import io.github.romvoid95.BotData;
import io.github.romvoid95.GalacticBot;
import io.github.romvoid95.commands.core.GalacticSlashCommand;
import io.github.romvoid95.database.entity.DBGalacticBot;
import io.github.romvoid95.database.impl.Suggestion;
import io.github.romvoid95.util.Embed;
import io.github.romvoid95.util.discord.Reply;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

public class DeleteSuggestion extends GalacticSlashCommand
{

    public DeleteSuggestion()
    {
        name("delete");
        description("[DM ONLY] Delete one of your suggestions");
        setOptions(RequiredOption.text("id", "The Unique ID provided to you by the Bot in DM's"));
        this.directMessagesAllowed();
    }

    @Override
    protected void onExecute(SlashCommandEvent event)
    {
        if (!event.getChannel().getType().equals(ChannelType.PRIVATE))
        {
            Reply.EphemeralReply(event, ResultLevel.ERROR, "This command can only be used in Private Channels");
            return;
        }
        String        id         = event.getOption("id").getAsString();
        DBGalacticBot db         = BotData.database().galacticBot();
        Suggestion    suggestion = db.getSuggestionFromUniqueId(id).get();

        event.replyEmbeds(Embed.descriptionEmbed("Are you sure you want to delete your suggestion with the title of:\n\n`" + suggestion.getTitle() + "`\n\n**THIS ACTION IS FINAL AND CANNOT BE REVERSED.\n\nClick the Confirm button below to confim suggestion deletion", RGB.ORANGE).toEmbed())
            .addActionRow(Button.danger("confirm", "Confirm Delete")).queue();
        
        GalacticBot.instance().getEventWaiter().waitForEvent(
            ButtonInteractionEvent.class, 
            e -> e.getComponentId().equals("confirm"), 
            e -> this.runDeleteEvent(suggestion, db, e), 
            1, TimeUnit.MINUTES, 
            null);
    }
    
    private void runDeleteEvent(Suggestion suggestion, DBGalacticBot db, ButtonInteractionEvent event)
    {
        try
        {
            boolean isPopular = suggestion.getMessages().communityPopularMsg().isPresent();

            if (isPopular)
            {
                String      popularMsgId            = suggestion.getMessages().getCommunityPopularMsgId();
                TextChannel communityPopularChannel = db.getSuggestionOptions().getPopularChannel();

                String      devPopularMsgId   = suggestion.getMessages().getDevPopularMsgId();
                TextChannel devPopularChannel = db.getSuggestionOptions().getDevPopularChannel();

                communityPopularChannel.deleteMessageById(popularMsgId).queue();
                devPopularChannel.deleteMessageById(devPopularMsgId).queue();
            }

            TextChannel txtChannel = db.getSuggestionOptions().getSuggestionChannel();
            txtChannel.deleteMessageById(suggestion.getMessages().getPostMsgId()).queue(s ->
            {
                event.editButton(null).and(event.editMessage("Suggestion sucessfully deleted")).queue();
                return;
            });
        } catch (Exception e)
        {
            event.editButton(null).and(event.editMessage("An error occoured when attempting to delete suggestion")).queue();
            return;
        }
    }
}
