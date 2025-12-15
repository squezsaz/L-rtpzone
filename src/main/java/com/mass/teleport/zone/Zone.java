package com.mass.teleport.zone;

import org.bukkit.Location;
import org.bukkit.World;

public class Zone {
    private String name;
    private Location pos1;
    private Location pos2;
    private Location center;
    private int taskId;
    
    public Zone(String name, Location pos1, Location pos2) {
        this.name = name;
        this.pos1 = pos1;
        this.pos2 = pos2;
        
        // Calculate center position
        double centerX = (pos1.getX() + pos2.getX()) / 2;
        double centerY = (pos1.getY() + pos2.getY()) / 2;
        double centerZ = (pos1.getZ() + pos2.getZ()) / 2;
        World world = pos1.getWorld();
        
        this.center = new Location(world, centerX, centerY, centerZ);
        this.taskId = -1;
    }
    
    public String getName() {
        return name;
    }
    
    public Location getPos1() {
        return pos1;
    }
    
    public Location getPos2() {
        return pos2;
    }
    
    public Location getCenter() {
        return center;
    }
    
    public boolean isInZone(Location loc) {
        if (!loc.getWorld().equals(pos1.getWorld())) return false;
        
        double x = loc.getX();
        double y = loc.getY();
        double z = loc.getZ();
        
        double minX = Math.min(pos1.getX(), pos2.getX());
        double maxX = Math.max(pos1.getX(), pos2.getX());
        double minY = Math.min(pos1.getY(), pos2.getY());
        double maxY = Math.max(pos1.getY(), pos2.getY());
        double minZ = Math.min(pos1.getZ(), pos2.getZ());
        double maxZ = Math.max(pos1.getZ(), pos2.getZ());
        
        return x >= minX && x <= maxX && y >= minY && y <= maxY && z >= minZ && z <= maxZ;
    }
    
    public int getTaskId() {
        return taskId;
    }
    
    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }
}