package com.orqadian.bridgebot;

import org.bukkit.plugin.java.JavaPlugin;


public class BridgeBotPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("AternosBridge has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("AternosBridge has been disabled.");
    }
}