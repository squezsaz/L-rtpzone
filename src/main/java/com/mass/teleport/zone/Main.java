package com.mass.teleport.zone;

import com.mass.teleport.zone.placeholders.ZonePlaceholderExpansion;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Main extends JavaPlugin {

    private static Main instance;
    private ZoneManager zoneManager;
    private PlayerListener playerListener;
    private Map<UUID, String> playerZones; // Track which zone each player is in
    private LanguageManager languageManager;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        // Initialize language manager first
        languageManager = new LanguageManager(this);
        
        zoneManager = new ZoneManager(this);
        playerListener = new PlayerListener(this);
        playerZones = new HashMap<>();

        this.getCommand("mtz").setExecutor(new MTZCommand(this));
        getServer().getPluginManager().registerEvents(playerListener, this);

        // PlaceholderAPI
        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new ZonePlaceholderExpansion(this).register();
            getLogger().info("PlaceholderAPI expansion registered!");
        }

        getLogger().info("L-rtpzone plugin enabled!");
    }

    @Override
    public void onDisable() {
        if (zoneManager != null) {
            zoneManager.shutdown();
        }
        getLogger().info("L-rtpzone plugin disabled!");
    }

    public static Main getInstance() {
        return instance;
    }

    public ZoneManager getZoneManager() {
        return zoneManager;
    }

    public PlayerListener getPlayerListener() {
        return playerListener;
    }

    public Map<UUID, String> getPlayerZones() {
        return playerZones;
    }
    
    public LanguageManager getLanguageManager() {
        return languageManager;
    }
}