package io.github.romvoid95.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;


public class HttpClient
{
    private static String HASTEBIN_SERVER = "https://paste.galacticraft.net/"; // requires trailing slash
    private static final Gson GSON = new GsonBuilder().serializeNulls().create();

    /**
     * A simple implementation of the Hastebin Client API, allowing data to be pasted online for
     * players to access.
     *
     * @param urlParameters The string to be sent in the body of the POST request
     * @return A formatted URL which links to the pasted file
     */
    public synchronized static String paste(String urlParameters) {
        HttpURLConnection connection = null;
        String output = null;
        try {
            //Create connection
            URL url = new URL(HASTEBIN_SERVER + "documents");
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setConnectTimeout(0);
            
            //Send request
            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();

            //Get Response
            BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            _200HasteResponse response = GSON.fromJson(rd.readLine(), _200HasteResponse.class);
            output = HASTEBIN_SERVER + response.getKey();

        } catch (IOException e) {

        } finally {
            connection.disconnect();
        }
        return output;
    }

    @Getter
    public static class _200HasteResponse
    {
        @SerializedName("key")
        private String key;
    }
}
