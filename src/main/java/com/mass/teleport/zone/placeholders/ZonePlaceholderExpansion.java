package com.mass.teleport.zone.placeholders;

import com.mass.teleport.zone.Main;
import com.mass.teleport.zone.Zone;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;

import java.util.Collection;

public class ZonePlaceholderExpansion extends PlaceholderExpansion {

    private final Main plugin;

    public ZonePlaceholderExpansion(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getIdentifier() {
        return "lrtpzone";
    }

    @Override
    public String getAuthor() {
        return "L-rtpzone";
    }

    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        if (player == null) return "";

        // Handle non-zone specific placeholders
        switch (identifier.toLowerCase()) {
            case "zone_count":
                return String.valueOf(plugin.getZoneManager().getAllZones().size());

            case "nearest_zone":
                return getNearestZoneName(player);

            case "nearest_zone_time":
                return String.valueOf(getNearestZoneTime(player));
        }

        // Handle zone-specific placeholders
        if (identifier.startsWith("zone_")) {
            // Remove the "lrtpzone_" prefix if present
            String zoneIdentifier = identifier;
            if (identifier.startsWith("lrtpzone_zone_")) {
                zoneIdentifier = identifier.substring("lrtpzone_".length());
            }
            
            // Split the identifier to parse zone name and property
            String[] parts = zoneIdentifier.split("_");
            
            // We need at least "zone", "<name>", and "<property>"
            if (parts.length >= 3 && "zone".equals(parts[0])) {
                // Extract the property (last part)
                String property = parts[parts.length - 1];
                
                // Extract zone name (everything between "zone" and the property)
                StringBuilder zoneNameBuilder = new StringBuilder();
                for (int i = 1; i < parts.length - 1; i++) {
                    if (zoneNameBuilder.length() > 0) {
                        zoneNameBuilder.append("_");
                    }
                    zoneNameBuilder.append(parts[i]);
                }
                String zoneName = zoneNameBuilder.toString();
                
                // Get the zone
                Zone zone = plugin.getZoneManager().getZone(zoneName);
                if (zone != null) {
                    switch (property.toLowerCase()) {
                        case "players":
                            return String.valueOf(getPlayersInZone(zone));
                        case "countdown":
                            return String.valueOf(getZoneCountdown(zone));
                    }
                }
            }
        }
        
        return null;
    }

    private String getNearestZoneName(Player player) {
        Collection<Zone> zones = plugin.getZoneManager().getAllZones();
        Zone nearest = null;
        double nearestDistance = Double.MAX_VALUE;

        for (Zone zone : zones) {
            if (!player.getWorld().equals(zone.getCenter().getWorld())) continue;

            double distance = player.getLocation().distance(zone.getCenter());
            if (distance < nearestDistance) {
                nearestDistance = distance;
                nearest = zone;
            }
        }

        return nearest != null ? nearest.getName() : "None";
    }

    private int getNearestZoneTime(Player player) {
        Collection<Zone> zones = plugin.getZoneManager().getAllZones();
        Zone nearest = null;
        double nearestDistance = Double.MAX_VALUE;

        for (Zone zone : zones) {
            if (!player.getWorld().equals(zone.getCenter().getWorld())) continue;

            double distance = player.getLocation().distance(zone.getCenter());
            if (distance < nearestDistance) {
                nearestDistance = distance;
                nearest = zone;
            }
        }

        if (nearest != null) {
            return getZoneCountdown(nearest);
        }

        return 0;
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

    private int getZoneCountdown(Zone zone) {
        return plugin.getZoneManager().getZoneCountdown(zone.getName());
    }
}