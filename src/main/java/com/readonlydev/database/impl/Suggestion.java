package com.readonlydev.database.impl;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.readonlydev.util.discord.SetChannel;
import com.readonlydev.util.discord.SuggestionStatus;
import com.readonlydev.util.rec.LinkedMessagesRecord;
import com.readonlydev.util.style.To;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.requests.RestAction;

@NoArgsConstructor
@Getter
@Setter
public class Suggestion
{

    public static final Suggestion NULL = new Suggestion(null, null, null);

    private String                 _id;
    private String                 authorId;
    private String                 title;
    private int                    upvotes;
    private SuggestionStatus       status;
    private LinkedMessages         messages;

    public Suggestion(String postMsgId, String authorId, String title)
    {
        this(postMsgId, authorId, title, 0);
    }

    public Suggestion(String postMsgId, String authorId, String title, Integer upvotes)
    {
        super();
        this.authorId = authorId;
        this.title = title;
        this.upvotes = upvotes;
        this.status = SuggestionStatus.NONE;
        this.messages = new LinkedMessages(postMsgId);
    }
    
    public String postMsgId()
    {
        return this.getMessages().getPostMsgId();
    }
    
    @Override
    public String toString()
    {
        return To.String(this);
    }
    
    @NoArgsConstructor
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class LinkedMessages {
        @Getter @Setter
        private String postMsgId;
        @Getter @Setter
        private String communityPopularMsgId;
        @Getter @Setter
        private String devPopularMsgId;
        
        public LinkedMessages(String postMsgId)
        {
            this(postMsgId, "", "");
        }
        
        @JsonIgnore
        public Optional<Message> postMsg()
        {
            return getMessage(SetChannel.POST.getChannel().retrieveMessageById(postMsgId));
        }
        
        @JsonIgnore
        public Optional<Message> communityPopularMsg()
        {
            if(getCommunityPopularMsgId().isEmpty())
            {
                return Optional.empty();
            }
            return getMessage(SetChannel.POPULAR.getChannel().retrieveMessageById(communityPopularMsgId));
        }
        
        @JsonIgnore
        public Optional<Message> devPopularMsg()
        {
            if(getCommunityPopularMsgId().isEmpty())
            {
                return Optional.empty();
            }
            return getMessage(SetChannel.DEV_POPULAR.getChannel().retrieveMessageById(devPopularMsgId));
        }
        
        @JsonIgnore
        public LinkedMessagesRecord getLinkedMessagesRecord()
        {
            return new LinkedMessagesRecord(postMsg(), communityPopularMsg(), devPopularMsg());
        }
        
        private Optional<Message> getMessage(RestAction<Message> action)
        {

            return Optional.ofNullable(action.complete());
        }
        
        @Override
        public String toString()
        {
            return To.String(this);
        }
    }
}
