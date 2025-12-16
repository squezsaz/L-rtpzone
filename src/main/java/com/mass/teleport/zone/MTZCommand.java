package com.mass.teleport.zone;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.UUID;
import java.util.Collection;
import org.bukkit.Bukkit;
import java.util.HashMap;
import java.util.Map;

// Added imports for clickable messages
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;

public class MTZCommand implements CommandExecutor {
    private Main plugin;
    
    public MTZCommand(Main plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                plugin.getLanguageManager().getMessage("message.command.only_player")));
            return true;
        }
        
        Player player = (Player) sender;
        
        // Check if player has admin permission for all commands
        if (!player.hasPermission("l-rtpzone.admin")) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                plugin.getLanguageManager().getMessage("message.command.no_permission")));
            return true;
        }
        
        // Handle reload command separately since it doesn't require player to be in a zone
        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            handleReload(sender);
            return true;
        }
        
        if (args.length == 0) {
            sendHelp(player);
            return true;
        }
        
        switch (args[0].toLowerCase()) {
            case "create":
                handleCreate(player, args);
                break;
            case "remove":
                handleRemove(player, args);
                break;
            case "list":
                handleList(player);
                break;
            case "pos1":
                handlePos1(player);
                break;
            case "pos2":
                handlePos2(player);
                break;
            case "info":
                handleInfo(player);
                break;
            case "teleport":
                handleTeleport(player, args);
                break;
            default:
                sendHelp(player);
                break;
        }
        
        return true;
    }
    
    private void sendHelp(Player player) {
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
            plugin.getLanguageManager().getMessage("message.help.header")));
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
            plugin.getLanguageManager().getMessage("message.help.create")));
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
            plugin.getLanguageManager().getMessage("message.help.remove")));
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
            plugin.getLanguageManager().getMessage("message.help.list")));
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
            plugin.getLanguageManager().getMessage("message.help.pos1")));
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
            plugin.getLanguageManager().getMessage("message.help.pos2")));
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
            plugin.getLanguageManager().getMessage("message.help.info")));
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
            plugin.getLanguageManager().getMessage("message.help.teleport")));
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
            plugin.getLanguageManager().getMessage("message.help.reload")));
    }
    
    private void handleCreate(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                plugin.getLanguageManager().getMessage("message.command.usage.create")));
            return;
        }
        
        String name = args[1];
        
        if (plugin.getZoneManager().zoneExists(name)) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                plugin.getLanguageManager().getMessage("message.command.zone.exists")));
            return;
        }
        
        // Check if player has selected both positions
        UUID playerId = player.getUniqueId();
        if (!plugin.getPlayerListener().hasBothPositionsSelected(playerId)) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                plugin.getLanguageManager().getMessage("message.command.missing_positions")));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                plugin.getLanguageManager().getMessage("message.command.missing_positions_help")));
            return;
        }
        
        // Get the selected positions
        Location pos1 = plugin.getPlayerListener().getPos1(playerId);
        Location pos2 = plugin.getPlayerListener().getPos2(playerId);
        
        // Clear the selections
        plugin.getPlayerListener().clearSelections(playerId);
        
        plugin.getZoneManager().createZone(name, pos1, pos2);
        
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("name", name);
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
            plugin.getLanguageManager().getMessage("message.command.zone.created", placeholders)));
    }
    
    private void handleRemove(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                plugin.getLanguageManager().getMessage("message.command.usage.remove")));
            return;
        }
        
        String name = args[1];
        
        if (!plugin.getZoneManager().zoneExists(name)) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                plugin.getLanguageManager().getMessage("message.command.zone.not_exists")));
            return;
        }
        
        plugin.getZoneManager().removeZone(name);
        
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("name", name);
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
            plugin.getLanguageManager().getMessage("message.command.zone.removed", placeholders)));
    }
    
    private void handleList(Player player) {
        Collection<Zone> zones = plugin.getZoneManager().getAllZones();
        
        if (zones.isEmpty()) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                plugin.getLanguageManager().getMessage("message.command.no_zones")));
        } else {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                plugin.getLanguageManager().getMessage("message.help.header").replace("Commands", "Zones")));
            for (Zone zone : zones) {
                player.sendMessage(ChatColor.YELLOW + "- " + zone.getName());
            }
        }
    }
    
    private void handlePos1(Player player) {
        plugin.getZoneManager().addSelectingPos1(player.getUniqueId());
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
            plugin.getLanguageManager().getMessage("message.command.position.select1")));
    }
    
    private void handlePos2(Player player) {
        plugin.getZoneManager().addSelectingPos2(player.getUniqueId());
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
            plugin.getLanguageManager().getMessage("message.command.position.select2")));
    }
    
    private void handleInfo(Player player) {
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
            plugin.getLanguageManager().getMessage("message.info.header")));
        Collection<Zone> zones = plugin.getZoneManager().getAllZones();
        
        if (zones.isEmpty()) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                plugin.getLanguageManager().getMessage("message.command.no_zones")));
            return;
        }
        
        for (Zone zone : zones) {
            // Create clickable zone name
            TextComponent zoneComponent = new TextComponent(ChatColor.translateAlternateColorCodes('&', 
                plugin.getLanguageManager().getMessage("message.info.zone").replace("{name}", zone.getName())));
            zoneComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mtz teleport " + zone.getName()));
            zoneComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, 
                new ComponentBuilder("Click to teleport to this zone").create()));
            player.spigot().sendMessage(zoneComponent);
            
            Location pos1 = zone.getPos1();
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("x", String.valueOf(pos1.getBlockX()));
            placeholders.put("y", String.valueOf(pos1.getBlockY()));
            placeholders.put("z", String.valueOf(pos1.getBlockZ()));
            placeholders.put("world", pos1.getWorld().getName());
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                plugin.getLanguageManager().getMessage("message.info.position1", placeholders)));
            
            Location pos2 = zone.getPos2();
            placeholders.clear();
            placeholders.put("x", String.valueOf(pos2.getBlockX()));
            placeholders.put("y", String.valueOf(pos2.getBlockY()));
            placeholders.put("z", String.valueOf(pos2.getBlockZ()));
            placeholders.put("world", pos2.getWorld().getName());
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                plugin.getLanguageManager().getMessage("message.info.position2", placeholders)));
            
            Location center = zone.getCenter();
            placeholders.clear();
            placeholders.put("x", String.format("%.2f", center.getX()));
            placeholders.put("y", String.format("%.2f", center.getY()));
            placeholders.put("z", String.format("%.2f", center.getZ()));
            placeholders.put("world", center.getWorld().getName());
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                plugin.getLanguageManager().getMessage("message.info.center", placeholders)));
            
            placeholders.clear();
            placeholders.put("count", String.valueOf(getPlayersInZone(zone)));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                plugin.getLanguageManager().getMessage("message.info.players", placeholders)));
            
            placeholders.clear();
            placeholders.put("seconds", String.valueOf(plugin.getZoneManager().getZoneCountdown(zone.getName())));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                plugin.getLanguageManager().getMessage("message.info.countdown", placeholders)));
            
            player.sendMessage("");
        }
    }
    
    private void handleTeleport(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Usage: /mtz teleport <zone_name>");
            return;
        }
        
        String zoneName = args[1];
        Zone zone = plugin.getZoneManager().getZone(zoneName);
        
        if (zone == null) {
            player.sendMessage(ChatColor.RED + "Zone '" + zoneName + "' not found!");
            return;
        }
        
        // Teleport player to the center of the zone
        Location center = zone.getCenter();
        // Make sure the location is safe
        center = center.clone();
        center.setY(center.getWorld().getHighestBlockYAt(center) + 1);
        
        player.teleport(center);
        
        // Send localized message
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("name", zoneName);
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
            plugin.getLanguageManager().getMessage("message.teleport.to_zone", placeholders)));
    }
    
    private String formatLocation(Location loc) {
        if (loc == null) return "Unknown";
        return String.format("World: %s, X: %.2f, Y: %.2f, Z: %.2f", 
            loc.getWorld().getName(), loc.getX(), loc.getY(), loc.getZ());
    }
    
    private int getPlayersInZone(Zone zone) {
        int count = 0;
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (zone.isInZone(player.getLocation())) {
                count++;
            }
        }
        return count;
    }
    
    private void handleReload(CommandSender sender) {
        try {
            // Reload the config
            plugin.reloadConfig();
            
            // Reload the language manager
            plugin.getLanguageManager().loadLanguage();
            
            // Send success message
            sender.sendMessage(ChatColor.GREEN + "Plugin configuration and language files reloaded successfully!");
            
            // Log to console
            plugin.getLogger().info("Plugin configuration and language files reloaded by " + 
                (sender instanceof Player ? ((Player) sender).getName() : "console"));
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + "Error reloading plugin: " + e.getMessage());
            plugin.getLogger().severe("Error reloading plugin: " + e.getMessage());
            e.printStackTrace();
        }
    }
}