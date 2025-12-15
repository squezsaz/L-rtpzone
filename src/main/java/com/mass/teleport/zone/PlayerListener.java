package com.mass.teleport.zone;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.Set;
import java.util.HashSet;

public class PlayerListener implements Listener {
    private Main plugin;
    private Map<UUID, Location> pos1Selections;
    private Map<UUID, Location> pos2Selections;
    private Set<UUID> recentlyTeleportedPlayers; // Track players who were recently teleported

    public PlayerListener(Main plugin) {
        this.plugin = plugin;
        this.pos1Selections = new HashMap<>();
        this.pos2Selections = new HashMap<>();
        this.recentlyTeleportedPlayers = new HashSet<>();
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.LEFT_CLICK_BLOCK) return;

        UUID playerId = event.getPlayer().getUniqueId();

        if (plugin.getZoneManager().isSelectingPos1(playerId)) {
            event.setCancelled(true);
            pos1Selections.put(playerId, event.getClickedBlock().getLocation());
            plugin.getZoneManager().removeSelecting(playerId);
            event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', 
                plugin.getLanguageManager().getMessage("message.command.position.set1")));
        } else if (plugin.getZoneManager().isSelectingPos2(playerId)) {
            event.setCancelled(true);
            pos2Selections.put(playerId, event.getClickedBlock().getLocation());
            plugin.getZoneManager().removeSelecting(playerId);
            event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', 
                plugin.getLanguageManager().getMessage("message.command.position.set2")));
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        UUID playerId = event.getPlayer().getUniqueId();
        pos1Selections.remove(playerId);
        pos2Selections.remove(playerId);
        plugin.getZoneManager().removeSelecting(playerId);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();

        // Check if player moved to a different block
        if (event.getFrom().getBlockX() == event.getTo().getBlockX() &&
            event.getFrom().getBlockY() == event.getTo().getBlockY() &&
            event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }

        // Skip processing if player was recently teleported
        if (wasRecentlyTeleported(playerId)) {
            return;
        }

        // Check all zones
        for (Zone zone : plugin.getZoneManager().getAllZones()) {
            boolean wasInZone = plugin.getPlayerZones().containsKey(playerId) && 
                               plugin.getPlayerZones().get(playerId).equals(zone.getName());
            boolean isInZone = zone.isInZone(event.getTo());

            // Player entered the zone
            if (!wasInZone && isInZone) {
                plugin.getPlayerZones().put(playerId, zone.getName());
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                    plugin.getLanguageManager().getMessage("message.zone.enter")));
            }
            // Player exited the zone
            else if (wasInZone && !isInZone) {
                plugin.getPlayerZones().remove(playerId);
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                    plugin.getLanguageManager().getMessage("message.zone.exit")));
                // Debug: Log when player leaves zone after teleport
                if (wasRecentlyTeleported(playerId)) {
                    plugin.getLogger().info(player.getName() + " left zone " + zone.getName() + " shortly after teleportation");
                }
            }
        }
    }

    // Method to get selected positions for zone creation
    public Location getPos1(UUID playerId) {
        return pos1Selections.get(playerId);
    }

    public Location getPos2(UUID playerId) {
        return pos2Selections.get(playerId);
    }

    public boolean hasBothPositionsSelected(UUID playerId) {
        return pos1Selections.containsKey(playerId) && pos2Selections.containsKey(playerId);
    }

    public void clearSelections(UUID playerId) {
        pos1Selections.remove(playerId);
        pos2Selections.remove(playerId);
    }

    // Method to add a player to the recently teleported list
    public void addRecentlyTeleportedPlayer(UUID playerId) {
        recentlyTeleportedPlayers.add(playerId);

        // Remove the player from the list after 10 seconds
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            recentlyTeleportedPlayers.remove(playerId);
        }, 200L); // 10 seconds
    }

    // Method to check if a player was recently teleported
    public boolean wasRecentlyTeleported(UUID playerId) {
        return recentlyTeleportedPlayers.contains(playerId);
    }
}