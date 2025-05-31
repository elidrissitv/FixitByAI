package com.ests.fixitbyai;

import okhttp3.*;
import java.io.IOException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

public class HuggingFaceHelper {
    private static final String API_URL = "https://api-inference.huggingface.co/models/tiiuae/falcon-7b-instruct";

    public static String getIAResponse(String question) throws IOException {
        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json");
        String json = "{\"inputs\": \"" + question + "\"}";
        RequestBody body = RequestBody.create(json, mediaType);
        Request request = new Request.Builder()
                .url(API_URL)
                .post(body)
                .addHeader("Authorization", "Bearer " + Keys.HUGGINGFACE_API_TOKEN)
                .addHeader("Content-Type", "application/json")
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            String responseBody = response.body().string();
            // Parsing JSON robuste avec gestion d'erreur
            try {
                JSONArray arr = new JSONArray(responseBody);
                if (arr.length() > 0) {
                    JSONObject obj = arr.getJSONObject(0);
                    return obj.optString("generated_text", "Aucune réponse générée par l'IA.");
                } else {
                    return "Aucune réponse générée par l'IA.";
                }
            } catch (JSONException e) {
                return "Erreur de parsing JSON IA : " + e.getMessage();
            }
        }
    }
} 