package com.mass.teleport.zone;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class LanguageManager {
    private final JavaPlugin plugin;
    private Map<String, String> messages;
    private String currentLanguage;

    public LanguageManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.messages = new HashMap<>();
        this.currentLanguage = "en"; // Default language
        loadLanguage();
    }

    public void loadLanguage() {
        // Get language from config
        currentLanguage = plugin.getConfig().getString("language", "en");
        
        // Load the language file
        String fileName = "lang/lang_" + currentLanguage + ".yml";
        if ("en".equals(currentLanguage)) {
            fileName = "lang/lang.yml"; // Default English file has a different name
        }
        
        // Check if the language file exists in the plugin's resources
        InputStream inputStream = plugin.getResource(fileName);
        if (inputStream == null) {
            // Fallback to English if the requested language file doesn't exist
            plugin.getLogger().warning("Language file for '" + currentLanguage + "' not found, falling back to English.");
            currentLanguage = "en";
            fileName = "lang/lang.yml";
            inputStream = plugin.getResource(fileName);
        }
        
        if (inputStream != null) {
            try {
                // Load the language file
                YamlConfiguration langConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                messages.clear();
                
                // Load all messages into the map
                for (String key : langConfig.getKeys(true)) {
                    if (langConfig.isString(key)) {
                        messages.put(key, langConfig.getString(key));
                    }
                }
                
                plugin.getLogger().info("Loaded language file: " + fileName);
            } catch (Exception e) {
                plugin.getLogger().log(Level.SEVERE, "Failed to load language file: " + fileName, e);
            }
        } else {
            plugin.getLogger().severe("Could not load language file: " + fileName);
        }
    }

    public String getMessage(String key) {
        return messages.getOrDefault(key, "&cMissing translation: " + key);
    }

    public String getMessage(String key, Map<String, String> placeholders) {
        String message = messages.getOrDefault(key, "&cMissing translation: " + key);
        
        // Replace placeholders
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            message = message.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        
        return message;
    }

    public String getCurrentLanguage() {
        return currentLanguage;
    }
}