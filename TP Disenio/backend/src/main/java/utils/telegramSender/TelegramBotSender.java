package utils.telegramSender;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

public class TelegramBotSender {

    //TODO: SACAR ESTOS DATOS DESDE PROPERTIES
    private static final String BOT_TOKEN = "7048736649:AAG1cmqkpN2nR8w7V-tZERIfkuap0wftF2c";
    private static final String BASE_URL = "https://api.telegram.org/bot" + BOT_TOKEN;

    private final HttpClient client;

    public TelegramBotSender() {
        this.client = HttpClient.newHttpClient();
    }

    public JSONArray getConnectedUsers() throws Exception {
        String url = BASE_URL + "/getUpdates";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JSONObject jsonResponse = new JSONObject(response.body());

        JSONArray result = jsonResponse.optJSONArray("result");
        JSONArray chatIds = new JSONArray();

        if (result != null) {
            for (int i = 0; i < result.length(); i++) {
                JSONObject message = result.getJSONObject(i).getJSONObject("message");
                long chatId = message.getJSONObject("chat").getLong("id");
                if (!chatIds.toList().contains(chatId)) {
                    chatIds.put(chatId);
                }
            }
        }

        return chatIds;
    }

    public void sendMessage(long chatId, String message) throws Exception {
        String encodedMessage = URLEncoder.encode(message, StandardCharsets.UTF_8);
        String url = BASE_URL + "/sendMessage?chat_id=" + chatId + "&text=" + encodedMessage;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("Mensaje enviado a chat_id " + chatId + ": " + response.body());
    }

    public void sendBroadcastMessage(String message) throws Exception {
        JSONArray users = getConnectedUsers();
        for (int i = 0; i < users.length(); i++) {
            long chatId = users.getLong(i);
            sendMessage(chatId, message);
        }
    }
}
