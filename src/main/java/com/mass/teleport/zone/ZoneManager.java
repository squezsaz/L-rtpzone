package com.mass.teleport.zone;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.block.Biome;
import org.bukkit.Sound;

import java.util.UUID;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.Collection;

public class ZoneManager {
    private Main plugin;
    private Map<String, Zone> zones;
    private Set<UUID> selectingPos1;
    private Set<UUID> selectingPos2;
    private Map<String, Integer> zoneCountdowns; // Track countdowns for each zone
    
    public ZoneManager(Main plugin) {
        this.plugin = plugin;
        this.zones = new HashMap<>();
        this.selectingPos1 = new HashSet<>();
        this.selectingPos2 = new HashSet<>();
        this.zoneCountdowns = new HashMap<>();
        
        loadZones();
        startTeleportTasks();
    }
    
    public void createZone(String name, Location pos1, Location pos2) {
        Zone zone = new Zone(name, pos1, pos2);
        zones.put(name, zone);
        startZoneTask(zone);
        saveZones();
    }
    
    public void removeZone(String name) {
        Zone zone = zones.remove(name);
        if (zone != null) {
            // Cancel the teleport task
            if (zone.getTaskId() != -1) {
                Bukkit.getScheduler().cancelTask(zone.getTaskId());
            }
        }
        saveZones();
    }
    
    public Zone getZone(String name) {
        return zones.get(name);
    }
    
    public Collection<Zone> getAllZones() {
        return zones.values();
    }
    
    public boolean zoneExists(String name) {
        return zones.containsKey(name);
    }
    
    public void startZoneTask(Zone zone) {
        // Initialize countdown for this zone
        zoneCountdowns.put(zone.getName(), 45);
        
        // Schedule teleport task every 45 seconds
        int taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            int countdown = 45;
            
            @Override
            public void run() {
                // Update the countdown tracking
                zoneCountdowns.put(zone.getName(), countdown);
                
                if (countdown <= 0) {
                    // Teleport players in zone
                    teleportPlayersInZone(zone);
                    countdown = 45;
                } else {
                    countdown--;
                }
            }
        }, 0L, 20L); // Run every second (20 ticks = 1 second)
        
        zone.setTaskId(taskId);
    }
    
    private void teleportPlayersInZone(Zone zone) {
        // Get all players in the zone
        List<Player> playersToTeleport = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (zone.isInZone(player.getLocation())) {
                playersToTeleport.add(player);
            }
        }
        
        if (playersToTeleport.isEmpty()) {
            return;
        }
        
        // Get a random location in the world
        World world = zone.getPos1().getWorld();
        Location randomLoc = getRandomLocation(world);
        
        // Teleport all players to the random location
        for (Player player : playersToTeleport) {
            player.teleport(randomLoc);
            player.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&', 
                plugin.getLanguageManager().getMessage("message.teleport.safe_location")));
            // Play explosion sound effect
            player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.0f);
            // Show SAVAŞ! title
            player.sendTitle(org.bukkit.ChatColor.translateAlternateColorCodes('&', 
                plugin.getLanguageManager().getMessage("message.teleport.title")), "", 10, 40, 10);
            // Notify PlayerListener that this player was teleported
            plugin.getPlayerListener().addRecentlyTeleportedPlayer(player.getUniqueId());
        }
    }
    
    private Location getRandomLocation(World world) {
        // Get the plugin instance for config access
        Main pluginInstance = Main.getInstance();
        
        // Get world and coordinate limits from config
        String configWorldName = pluginInstance.getConfig().getString("teleport-world.world-name", "");
        int minX = pluginInstance.getConfig().getInt("teleport-world.min-x", -1000);
        int maxX = pluginInstance.getConfig().getInt("teleport-world.max-x", 1000);
        int minZ = pluginInstance.getConfig().getInt("teleport-world.min-z", -1000);
        int maxZ = pluginInstance.getConfig().getInt("teleport-world.max-z", 1000);
        
        // Determine the world to teleport to
        World teleportWorld = world; // Default to the same world as the zone
        if (!configWorldName.isEmpty()) {
            World configuredWorld = Bukkit.getWorld(configWorldName);
            if (configuredWorld != null) {
                teleportWorld = configuredWorld;
            } else {
                pluginInstance.getLogger().warning("Configured teleport world '" + configWorldName + "' not found, using zone world instead.");
            }
        }
        
        // Try up to 100 times to find a safe location
        for (int attempts = 0; attempts < 100; attempts++) {
            // Generate random coordinates within the configured bounds
            int x = minX + (int) (Math.random() * (maxX - minX));
            int z = minZ + (int) (Math.random() * (maxZ - minZ));
            
            // Find a safe y coordinate
            int y = teleportWorld.getHighestBlockYAt(x, z) + 1;
            Location loc = new Location(teleportWorld, x + 0.5, y, z + 0.5);
            
            // Check if the biome is safe
            Biome biome = teleportWorld.getBiome(loc);
            if (isSafeBiome(biome)) {
                return loc;
            }
        }
        
        // If we couldn't find a safe biome, just return a location within bounds
        int x = minX + (int) (Math.random() * (maxX - minX));
        int z = minZ + (int) (Math.random() * (maxZ - minZ));
        int y = teleportWorld.getHighestBlockYAt(x, z) + 1;
        return new Location(teleportWorld, x + 0.5, y, z + 0.5);
    }
    
    private boolean isSafeBiome(Biome biome) {
        // Define unsafe biomes
        switch (biome) {
            case NETHER_WASTES:
            case SOUL_SAND_VALLEY:
            case CRIMSON_FOREST:
            case WARPED_FOREST:
            case BASALT_DELTAS:
            case THE_VOID:
            case THE_END:
            case SMALL_END_ISLANDS:
            case END_MIDLANDS:
            case END_HIGHLANDS:
            case END_BARRENS:
            case WARM_OCEAN:
            case LUKEWARM_OCEAN:
            case DEEP_LUKEWARM_OCEAN:
            case OCEAN:
            case DEEP_OCEAN:
            case COLD_OCEAN:
            case DEEP_COLD_OCEAN:
            case FROZEN_OCEAN:
            case DEEP_FROZEN_OCEAN:
                return false;
            default:
                return true;
        }
    }
    
    public void startTeleportTasks() {
        for (Zone zone : zones.values()) {
            startZoneTask(zone);
        }
    }
    
    public void shutdown() {
        // Cancel all tasks
        for (Zone zone : zones.values()) {
            if (zone.getTaskId() != -1) {
                Bukkit.getScheduler().cancelTask(zone.getTaskId());
            }
        }
    }
    
    public int getZoneCountdown(String zoneName) {
        return zoneCountdowns.getOrDefault(zoneName, 45);
    }
    
    public void addSelectingPos1(UUID playerId) {
        selectingPos1.add(playerId);
    }
    
    public void addSelectingPos2(UUID playerId) {
        selectingPos2.add(playerId);
    }
    
    public boolean isSelectingPos1(UUID playerId) {
        return selectingPos1.contains(playerId);
    }
    
    public boolean isSelectingPos2(UUID playerId) {
        return selectingPos2.contains(playerId);
    }
    
    public void removeSelecting(UUID playerId) {
        selectingPos1.remove(playerId);
        selectingPos2.remove(playerId);
    }
    
    private void saveZones() {
        FileConfiguration config = plugin.getConfig();
        config.set("zones", null); // Clear existing zones
        
        for (Zone zone : zones.values()) {
            String path = "zones." + zone.getName();
            config.set(path + ".pos1.world", zone.getPos1().getWorld().getName());
            config.set(path + ".pos1.x", zone.getPos1().getX());
            config.set(path + ".pos1.y", zone.getPos1().getY());
            config.set(path + ".pos1.z", zone.getPos1().getZ());
            
            config.set(path + ".pos2.world", zone.getPos2().getWorld().getName());
            config.set(path + ".pos2.x", zone.getPos2().getX());
            config.set(path + ".pos2.y", zone.getPos2().getY());
            config.set(path + ".pos2.z", zone.getPos2().getZ());
        }
        
        plugin.saveConfig();
    }
    
    private void loadZones() {
        FileConfiguration config = plugin.getConfig();
        if (!config.contains("zones")) return;
        
        for (String zoneName : config.getConfigurationSection("zones").getKeys(false)) {
            String path = "zones." + zoneName;
            
            // Load pos1
            String world1Name = config.getString(path + ".pos1.world");
            World world1 = Bukkit.getWorld(world1Name);
            double x1 = config.getDouble(path + ".pos1.x");
            double y1 = config.getDouble(path + ".pos1.y");
            double z1 = config.getDouble(path + ".pos1.z");
            Location pos1 = new Location(world1, x1, y1, z1);
            
            // Load pos2
            String world2Name = config.getString(path + ".pos2.world");
            World world2 = Bukkit.getWorld(world2Name);
            double x2 = config.getDouble(path + ".pos2.x");
            double y2 = config.getDouble(path + ".pos2.y");
            double z2 = config.getDouble(path + ".pos2.z");
            Location pos2 = new Location(world2, x2, y2, z2);
            
            // Create zone
            Zone zone = new Zone(zoneName, pos1, pos2);
            zones.put(zoneName, zone);
        }
    }
}