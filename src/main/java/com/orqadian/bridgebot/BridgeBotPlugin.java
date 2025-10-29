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
    
    // --- FIX: Add a private field for the API URL ---
    // This variable belongs to the whole class now.
    private String apiUrl;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        
        // --- FIX: Assign the value to the class field, not a local variable ---
        this.apiUrl = getConfig().getString("api-url");

        // Now we check the class field.
        if (this.apiUrl == null || this.apiUrl.isEmpty()) {
            getLogger().severe("API URL is not set in config.yml! The plugin will not work.");
            return; // Stop the plugin from enabling.
        }

        getServer().getPluginManager().registerEvents(new ChatListener(this), this);

        getServer().getScheduler().runTaskTimerAsynchronously(this, () -> {
            try {
                // This now correctly uses the 'apiUrl' field from the class.
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