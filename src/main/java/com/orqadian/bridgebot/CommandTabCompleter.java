// src/main/java/com/orqadian/bridgebot/CommandTabCompleter.java
package com.orqadian.bridgebot;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CommandTabCompleter implements TabCompleter {

    private static final List<String> GAWR_SOUNDS = Arrays.asList(
            "ENTITY_CREEPER_PRIMED", "ENTITY_WITHER_SPAWN", "ENTITY_GHAST_SCREAM",
            "ENTITY_ENDERMAN_STARE", "BLOCK_PORTAL_TRIGGER", "ENTITY_ZOMBIE_VILLAGER_CURED",
            "BLOCK_ANVIL_LAND", "ENTITY_VILLAGER_NO", "ENTITY_ENDER_DRAGON_DEATH", "EVENT_RAID_HORN"
    );

    @Override 
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (command.getName().equalsIgnoreCase("gawr")) {
            return getGawrCompletions(args);
        }
        return new ArrayList<>();
    }

    private List<String> getGawrCompletions(String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            List<String> suggestions = getOnlinePlayerNames();
            suggestions.add("@a");
            StringUtil.copyPartialMatches(args[0], suggestions, completions);
        } else if (args.length == 2) {
            StringUtil.copyPartialMatches(args[1], GAWR_SOUNDS, completions);
        }
        return completions;
    }

    private List<String> getOnlinePlayerNames() {
        return Bukkit.getOnlinePlayers().stream()
                     .map(Player::getName)
                     .collect(Collectors.toList());
    }
}