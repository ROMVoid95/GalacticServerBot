package com.readonlydev;

import com.readonlydev.config.BotConfig;
import com.readonlydev.util.data.JsonDataManager;

public final class Conf
{
    private static JsonDataManager<BotConfig> botConfig;    
    
    private static JsonDataManager<BotConfig> jsonBotConfig() {
        if (botConfig == null) {
            botConfig = new JsonDataManager<>(BotConfig.class, "configs/bot.json", BotConfig::new);
        }

        return botConfig;
    }

    public static void saveBotConfigJson()
    {
        jsonBotConfig().save();
    }
    
    public static BotConfig Bot()
    {
        return jsonBotConfig().get();
    }
}
