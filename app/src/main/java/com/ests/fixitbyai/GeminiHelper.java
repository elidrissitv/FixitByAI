package com.ests.fixitbyai;

import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;

public class GeminiHelper {
    private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + Keys.GEMINI_API_KEY;
    private static final String REPAIR_CONTEXT = "Tu es un expert en réparation d'appareils électroniques, mécaniques et téléphones. Réponds uniquement aux questions concernant la réparation, le dépannage, l'entretien et les problèmes logiciels de ces appareils. Si la question n'est pas liée à la réparation, réponds poliment que tu ne peux répondre qu'aux questions de réparation. Question : ";

    public static String getGeminiResponse(String question) throws IOException {
        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json");
        String json = "{ \"contents\": [ { \"parts\": [ { \"text\": \"" + REPAIR_CONTEXT + question + "\" } ] } ] }";
        RequestBody body = RequestBody.create(json, mediaType);
        Request request = new Request.Builder()
                .url(API_URL)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            String responseBody = response.body().string();
            try {
                JSONObject obj = new JSONObject(responseBody);
                JSONArray candidates = obj.getJSONArray("candidates");
                if (candidates.length() > 0) {
                    JSONObject first = candidates.getJSONObject(0);
                    JSONObject content = first.getJSONObject("content");
                    JSONArray parts = content.getJSONArray("parts");
                    if (parts.length() > 0) {
                        return parts.getJSONObject(0).optString("text", "Aucune réponse générée.");
                    }
                }
                return "Aucune réponse générée.";
            } catch (JSONException e) {
                return "Erreur de parsing JSON Gemini : " + e.getMessage();
            }
        }
    }
} 