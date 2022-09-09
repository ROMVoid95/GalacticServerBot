package com.readonlydev.util.haste;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import com.readonlydev.BotData;
import com.readonlydev.GalacticBot;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.UtilityClass;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

@UtilityClass
public class HasteUtil {
	
	public static final OkHttpClient httpClient = new OkHttpClient();
	public static final Gson gson = new GsonBuilder().disableJdkUnsafe().create();
	
    public static String paste(String toSend) {

    	var url = BotData.database().botDatabase().getHasteOptions().getHasteUrl();
    	
        var post = RequestBody.create(toSend, MediaType.parse("application/json"));
        var toPost = new Request.Builder()
                .url(url  + "documents/")
                .header("User-Agent", GalacticBot.Info.USER_AGENT)
                .header("Content-Type", "application/json")
                .post(post)
                .build();

        try {
            try (var response = httpClient.newCall(toPost).execute()) {
                var body = response.body();
                if (body == null) {
                    throw new IllegalArgumentException();
                }

                var string = response.body().string();
                return string;
                //                return "https://paste.gg/p/anonymous/%s"
                //                        .formatted(new JSONObject(string).getJSONObject("result").getString("id"));
            }
        } catch (Exception e) {
            return "cannot post data to " + url;
        }
    }
    
    @Data
    @NoArgsConstructor
    static final class ReponseKey {
    	@SerializedName("key")
    	private String key;
    }
}
