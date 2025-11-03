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
    
    private String apiUrl;

    @Override
    public void onEnable() {

        saveDefaultConfig();
        
    
        this.apiUrl = getConfig().getString("api-url");

        if (this.apiUrl == null || this.apiUrl.isEmpty()) {
            getLogger().severe("API URL IS MISSING in config.yml! The plugin will not work.");
            return; 
        }

        getServer().getPluginManager().registerEvents(new ChatListener(this), this);


        getServer().getScheduler().runTaskTimerAsynchronously(this, () -> {
            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(this.apiUrl))
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
                getLogger().warning("Failed to fetch messages. Error: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            }
        }, 0L, 60L); 

        getLogger().info("BridgeBot Plugin is fully enabled!");
    }

    @Override
    public void onDisable() {
        getServer().getScheduler().cancelTasks(this);
        getLogger().info("BridgeBot Plugin has been disabled.");
    }
}
