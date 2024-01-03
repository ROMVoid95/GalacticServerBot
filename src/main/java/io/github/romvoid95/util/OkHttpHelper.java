package io.github.romvoid95.util;

import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OkHttpHelper
{
    private static final OkHttpClient CLIENT     = new OkHttpClient();
    private static final MediaType    MEDIA_TYPE = MediaType.parse("application/json");
    private static final HttpUrl      MAIN_URL   = HttpUrl.parse("https://paste.galacticraft.net");
    private static final HttpUrl      UPLOAD_URL = MAIN_URL.newBuilder().addPathSegment("documents").build();
    private static final Gson         GSON       = new GsonBuilder().serializeNulls().create();

    public static String postFile(String fileContent)
    {
        Request req = new Request.Builder()
            .url(UPLOAD_URL)
            .post(RequestBody.create(fileContent, OkHttpHelper.MEDIA_TYPE))
            .build();
        try (Response r = OkHttpHelper.CLIENT.newCall(req).execute())
        {
            if (!r.isSuccessful())
                return "Error encountered when posting file to HasteBin";
            _200HasteResponse haste = GSON.fromJson(r.body().string(), _200HasteResponse.class);
            return MAIN_URL.newBuilder().addEncodedPathSegment(haste.getKey()).build().toString();
        } catch (IOException e)
        {
            return "Error encountered when posting file to HasteBin";
        }
    }

    @Getter
    public static class _200HasteResponse
    {
        @SerializedName("key")
        private String key;
    }

    public static void main(String[] args)
    {
        System.out.println(postFile("test test test"));
    }
}
