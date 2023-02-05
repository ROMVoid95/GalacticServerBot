package io.github.romvoid95.logging;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.encoder.Encoder;
import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import io.github.romvoid95.Conf;

public class WebhookAppender extends AppenderBase<ILoggingEvent>
{
	private static final String WEBHOOK_START = "https://discord.com/api/webhooks/";
    private Encoder<ILoggingEvent> encoder;
    private WebhookClient client;

    @Override
    public void start() {
		String webhook = Conf.Bot().getWebhookUrl().replace(WEBHOOK_START, "");
		var parts = webhook.split("/");
		
		long webhookId = Long.parseUnsignedLong(parts[0]);
		String webhookToken = parts[1];
		
        client = new WebhookClientBuilder(webhookId, webhookToken).setDaemon(true).build();
        super.start();
    }

    @Override
    public void stop() {
        if(client != null)
            client.close();
        super.stop();
    }

    @Override
    protected void append(ILoggingEvent eventObject) {
        if(!isStarted())
            return;
        if(eventObject.getLoggerName().equals("club.minnced.discord.webhook.WebhookClient"))
            return;
        byte[] encode = encoder.encode(eventObject);
        String log = new String(encode);
        client.send(log.length() > 2000 ? log.substring(0, 1997) + "..." : log);
    }

    public Encoder<ILoggingEvent> getEncoder() {
        return encoder;
    }

    public void setEncoder(Encoder<ILoggingEvent> encoder) {
        this.encoder = encoder;
    }
}
