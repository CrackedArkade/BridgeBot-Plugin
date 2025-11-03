package com.orqadian.bridgebot;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class SoundPlayCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by a player.");
            return true;
        }

        Player troll = (Player) sender;

        if (!troll.hasPermission("bridgebot.gawr")) {
            troll.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }

        if (args.length != 2) {
            troll.sendMessage(ChatColor.RED + "Usage: /gawr <target|@a> <sound>");
            return false;
        }

        String targetName = args[0];
        String soundName = args[1];

        try {
            Sound soundToPlay = Sound.valueOf(soundName.toUpperCase());

            if (targetName.equalsIgnoreCase("@a")) {
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    Vector behindVector = onlinePlayer.getLocation().getDirection().multiply(-1).normalize();
                    Location soundLocation = onlinePlayer.getLocation().add(behindVector.multiply(2));
                    onlinePlayer.playSound(soundLocation, soundToPlay, 1.0f, 1.0f);
                }
                troll.sendMessage(ChatColor.GREEN + "Played '" + soundToPlay.name() + "' for everyone.");
            
            } else {
                Player target = Bukkit.getPlayer(targetName);

                if (target == null) {
                    troll.sendMessage(ChatColor.RED + "Player '" + targetName + "' is not online.");
                    return true;
                }
                
                Vector behindVector = target.getLocation().getDirection().multiply(-1).normalize();
                Location soundLocation = target.getLocation().add(behindVector.multiply(2));
                
                target.playSound(soundLocation, soundToPlay, 1.0f, 1.0f);
                troll.sendMessage(ChatColor.GREEN + "Played '" + soundToPlay.name() + "' for " + target.getName() + ".");
            }

        } catch (IllegalArgumentException e) {
            troll.sendMessage(ChatColor.RED + "Invalid sound: '" + soundName + "'. Use tab-complete for options.");
        }

        return true;
    }
}