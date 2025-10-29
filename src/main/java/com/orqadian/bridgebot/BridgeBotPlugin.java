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

    // Create these objects once, so we can reuse them.
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final Gson gson = new Gson();

    @Override
    public void onEnable() {
        // --- Setup Configuration ---
        saveDefaultConfig();
        String apiUrl = getConfig().getString("api-url");

        // A quick check to remind the user to set the URL
        if (apiUrl == null || apiUrl.contains("localhost")) {
            getLogger().warning("API URL is set to localhost. This is fine for local testing, but remember to change it for a live server!");
        }

        // --- Register the Chat Listener ---
        getServer().getPluginManager().registerEvents(new ChatListener(this), this);

        // --- Schedule the Polling Task ---
        // This task will run in the background to fetch messages from Discord.
        getServer().getScheduler().runTaskTimerAsynchronously(this, () -> {
            try {
                // Build the GET request to our .NET API's endpoint
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(apiUrl))
                        .GET()
                        .build();

                // Send the request and wait for the response
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                // The response body is a JSON array of strings: e.g., ["msg1", "msg2"]
                // We need to tell Gson what kind of list to expect.
                Type listType = new TypeToken<List<String>>(){}.getType();
                List<String> messages = gson.fromJson(response.body(), listType);

                // If we got any messages, we need to show them in the game.
                if (messages != null && !messages.isEmpty()) {
                    // IMPORTANT: Any action that affects the game world (like broadcasting a message)
                    // must be run on the main server thread. This schedules the task to do so.
                    getServer().getScheduler().runTask(this, () -> {
                        for (String message : messages) {
                            Bukkit.broadcastMessage(message);
                        }
                    });
                }
            } catch (Exception e) {
                // Don't spam the console if the API is down, just log it occasionally.
                // For now, we'll log every error.
                getLogger().warning("Failed to fetch messages from Discord bridge: " + e.getMessage());
            }
        }, 0L, 60L); // 0L = delay before first run, 60L = ticks between runs (20 ticks ~ 1 second, so 60 ticks ~ 3 seconds)

        getLogger().info("BridgeBot Plugin has been enabled and is fully operational!");
    }

    @Override
    public void onDisable() {
        // Cancel all scheduled tasks when the plugin is disabled
        getServer().getScheduler().cancelTasks(this);
        getLogger().info("BridgeBot Plugin has been disabled.");
    }
}