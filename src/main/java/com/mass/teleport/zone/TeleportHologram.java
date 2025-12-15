package com.mass.teleport.zone;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;

public class TeleportHologram {
    private ArmorStand hologram;
    
    public TeleportHologram(Location location) {
        // Spawn armor stand at location
        hologram = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        
        // Configure armor stand
        hologram.setGravity(false);
        hologram.setCanPickupItems(false);
        hologram.setCustomNameVisible(true);
        hologram.setVisible(false);
        hologram.setMarker(true);
        hologram.setSilent(true);
        
        // Set initial text
        hologram.setCustomName("§aMass Teleport\n§eNext teleport: §c45s");
    }
    
    public void updateText(String text) {
        if (hologram != null && !hologram.isDead()) {
            hologram.setCustomName(text);
        }
    }
    
    public void remove() {
        if (hologram != null && !hologram.isDead()) {
            hologram.remove();
        }
    }
    
    public ArmorStand getHologram() {
        return hologram;
    }
}