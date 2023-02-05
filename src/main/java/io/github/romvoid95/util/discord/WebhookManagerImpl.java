package io.github.romvoid95.util.discord;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.jetbrains.annotations.Nullable;

import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.external.JDAWebhookClient;
import club.minnced.discord.webhook.send.AllowedMentions;
import club.minnced.discord.webhook.send.WebhookMessage;
import io.github.romvoid95.util.Factory;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.attribute.IWebhookContainer;
import net.dv8tion.jda.api.entities.channel.concrete.NewsChannel;
import okhttp3.OkHttpClient;

public class WebhookManagerImpl implements WebhookManager
{
    private static final List<WebhookManagerImpl> MANAGERS = new CopyOnWriteArrayList<>();
    private static final OkHttpClient HTTP_CLIENT = new OkHttpClient();
    private static ScheduledExecutorService executor;

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() ->
            MANAGERS.forEach(WebhookManagerImpl::close), "WebhookClosing"));
    }

    private static ScheduledExecutorService getExecutor() {
        if (executor == null) {
            executor = Factory.newScheduledThreadPool(Math.max(MANAGERS.size() / 3, 1), "Webhooks", true);
            // Clear webhooks after 6 hours to refresh them
            getExecutor().scheduleAtFixedRate(() -> MANAGERS.forEach(it -> it.webhooks.clear()), 1, 6, TimeUnit.HOURS);
        }
        return executor;
    }

    private final Predicate<String> predicate;
    private final String webhookName;
    private final AllowedMentions allowedMentions;
    private final Long2ObjectMap<JDAWebhookClient> webhooks = new Long2ObjectOpenHashMap<>();
    @Nullable
    private final Consumer<Webhook> creationListener;

    public WebhookManagerImpl(final Predicate<String> predicate, final String webhookName, final AllowedMentions allowedMentions, @Nullable final Consumer<Webhook> creationListener) {
        this.predicate = predicate;
        this.webhookName = webhookName;
        this.allowedMentions = allowedMentions;
        this.creationListener = creationListener;
        MANAGERS.add(this);
    }

    @Override
    public JDAWebhookClient getWebhook(final IWebhookContainer channel) {
        return webhooks.computeIfAbsent(channel.getIdLong(), k ->
            WebhookClientBuilder.fromJDA(getOrCreateWebhook(channel))
                .setExecutorService(getExecutor())
                .setHttpClient(HTTP_CLIENT)
                .setAllowedMentions(allowedMentions)
                .buildJDA());
    }

    @Override
    public void sendAndCrosspost(final IWebhookContainer channel, final WebhookMessage message) {
        getWebhook(channel)
            .send(message)
            .thenAccept(msg -> {
                if (channel.getType() == ChannelType.NEWS) {
                    ((NewsChannel) channel).retrieveMessageById(msg.getId()).flatMap(Message::crosspost).queue();
                }
            });
    }
    
    @Override
    public void send(IWebhookContainer channel, WebhookMessage message)
    {
        getWebhook(channel).send(message);
    }

    private Webhook getOrCreateWebhook(IWebhookContainer channel) {
        final var alreadyExisted = unwrap(Objects.requireNonNull(channel).retrieveWebhooks()
            .submit(false))
            .stream()
            .filter(w -> predicate.test(w.getName()))
            .findAny();
        return alreadyExisted.orElseGet(() -> {
            final var webhook = unwrap(channel.createWebhook(webhookName).submit(false));
            if (creationListener != null) {
                creationListener.accept(webhook);
            }
            return webhook;
        });
    }

    private static <T> T unwrap(CompletableFuture<T> completableFuture) {
        try {
            return completableFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public void close() {
        webhooks.forEach((id, client) -> client.close());
    }
}
