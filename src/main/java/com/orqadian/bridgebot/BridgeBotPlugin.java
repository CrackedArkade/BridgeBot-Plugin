package com.orqadian.bridgebot;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class BridgeBotPlugin extends JavaPlugin {


    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final Gson gson = new Gson();

    @Override
    public void onEnable() {

        saveDefaultConfig();
        String apiUrl = getConfig().getString("api-url");

        if (apiUrl == null || apiUrl.contains("localhost")) {
            getLogger().warning("API URL is set to a default value. This is fine for local testing!");
        }

        getServer().getPluginManager().registerEvents(new ChatListener(this), this);


        getServer().getScheduler().runTaskTimerAsynchronously(this, () -> {
            try {

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(apiUrl))
                        .GET()
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());


                Type listType = new TypeToken<List<String>>(){}.getType();
                List<String> messages = gson.fromJson(response.body(), listType);

                if (messages != null && !messages.isEmpty()) {

                    getServer().getScheduler().runTask(this, () -> {
                        for (String message : messages) {
                            Bukkit.broadcastMessage(message);
                        }
                    });
                }
            } catch (Exception e) {

                getLogger().warning("Failed to fetch messages from Discord bridge: " + e.getMessage());
            }
        }, 0L, 60L); 

        getLogger().info("BridgeBot Plugin has been enabled and is fully operational!");
    }

    @Override
    public void onDisable() {

        getServer().getScheduler().cancelTasks(this);
        getLogger().info("BridgeBot Plugin has been disabled.");
    }
}