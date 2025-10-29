package com.orqadian.bridgebot;

import com.google.gson.Gson;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ChatListener implements Listener {

    private final BridgeBotPlugin plugin;
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final Gson gson = new Gson();
    private final String apiUrl;


    public ChatListener(BridgeBotPlugin plugin) {
        this.plugin = plugin;

        this.apiUrl = plugin.getConfig().getString("api-url");
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        String playerName = event.getPlayer().getName();
        String message = event.getMessage();

        MinecraftMessage payload = new MinecraftMessage(playerName, message);
        String jsonPayload = gson.toJson(payload);

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();

            httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());

        } catch (Exception e) {
            plugin.getLogger().warning("Failed to send message to Discord bridge: " + e.getMessage());
        }
    }


    private static class MinecraftMessage {
        String playerName;
        String content;

        MinecraftMessage(String playerName, String content) {
            this.playerName = playerName;
            this.content = content;
        }
    }
}