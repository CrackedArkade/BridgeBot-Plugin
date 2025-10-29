package com.orqadian.bridgebot;

import org.bukkit.plugin.java.JavaPlugin;

public class BridgeBotPlugin extends JavaPlugin {

    @Override
    public void onEnable() {

        saveDefaultConfig();

        getLogger().info("BridgeBotPlugin has been enabled and config is loaded!");
    }

    @Override
    public void onDisable() {
        getLogger().info("BridgeBotPlugin has been disabled.");
    }
}