package io.github.romvoid95.util.rec;

import java.util.Optional;

import io.github.romvoid95.util.discord.entity.SuggestionMessage;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import net.dv8tion.jda.api.requests.restaction.MessageEditAction;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;
import net.dv8tion.jda.api.utils.messages.MessageEditData;

public record LinkedMessagesRecord(Optional<Message> postMsg, Optional<Message> communityMsg, Optional<Message> devMsg)
{

    public MessageEditAction editMessages(SuggestionMessage embed)
    {
        MessageEditBuilder builder = new MessageEditBuilder().applyCreateData(embed.toData());

        MessageEditData editData = builder.build();

        if (communityMsg().isPresent())
        {
            communityMsg().get().editMessage(editData).queue();
        }
        if (devMsg().isPresent())
        {
            devMsg().get().editMessage(editData).queue();
        }

        return postMsg().get().editMessage(editData);
    }

    public AuditableRestAction<Void> deleteMessages()
    {
        if (communityMsg().isPresent())
        {
            communityMsg().get().delete().queue();
        }
        if (devMsg().isPresent())
        {
            devMsg().get().delete().queue();
        }

        return postMsg.get().delete();
    }
}
