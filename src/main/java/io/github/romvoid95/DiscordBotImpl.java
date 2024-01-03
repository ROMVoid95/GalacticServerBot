package io.github.romvoid95;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import io.github.readonly.api.BotContainer;
import io.github.readonly.command.Client;
import io.github.readonly.command.ClientBuilder;
import io.github.readonly.common.waiter.EventWaiter;
import io.github.readonly.discordbot.DiscordBot;

public abstract class DiscordBotImpl<T> implements BotContainer {
    public static final String VERSION;
    
    static {
      String version = DiscordBot.class.getPackage().getImplementationVersion();
      if (version == null)
        version = "DEV " + DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(OffsetDateTime.now(ZoneOffset.UTC)); 
      VERSION = version;
    }
    
    private final EventWaiter eventWaiter = new EventWaiter();
    
    public EventWaiter getEventWaiter() {
      return this.eventWaiter;
    }
    
    private final ClientBuilder clientBuilder = new ClientBuilder();
    
    private Client client;
    
    public ClientBuilder getClientBuilder() {
      return this.clientBuilder;
    }
    
    public Client getClient() {
      return this.client;
    }
    
    protected Client buildClient() {
      return this.client = getClientBuilder().build();
    }
  }
