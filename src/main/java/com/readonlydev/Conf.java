package com.readonlydev;

import com.readonlydev.config.BotConfig;
import com.readonlydev.util.data.JsonDataManager;

public final class Conf
{
    private static JsonDataManager<BotConfig> botConfig;    
    //private static JsonDataManager<UpdateConfig> updateConfig;
    
    private static JsonDataManager<BotConfig> jsonBotConfig() {
        if (botConfig == null) {
            botConfig = new JsonDataManager<>(BotConfig.class, "configs/bot.json", BotConfig::new);
        }

        return botConfig;
    }
    
//    private static JsonDataManager<UpdateConfig> jsonUpdateConfig() {
//        if (updateConfig == null) {
//            updateConfig = new JsonDataManager<>(UpdateConfig.class, "configs/update.json", UpdateConfig::new);
//        }
//
//        return updateConfig;
//    }

    public static void saveBotConfigJson()
    {
        jsonBotConfig().save();
    }
    
//    public static void saveUpdateConfigJson()
//    {
//        jsonUpdateConfig().save();
//    }
    
    public static BotConfig Bot()
    {
        return jsonBotConfig().get();
    }

//    public static UpdateConfig Update()
//    {
//        return jsonUpdateConfig().get();
//    }
}
