package com.adventureplanner;

import net.runelite.api.coords.WorldPoint;

public final class ShootingStarSighting
{
    private final int world;
    private final WorldPoint point;
    private final String location;
    private final int tier;
    private final long firstSeenAt;
    private long lastSeenAt;
    private boolean active;

    ShootingStarSighting(int world, WorldPoint point, String location, int tier, long seenAt)
    {
        this.world = world;
        this.point = point;
        this.location = location;
        this.tier = tier;
        this.firstSeenAt = seenAt;
        this.lastSeenAt = seenAt;
        this.active = true;
    }

    void seen(long now)
    {
        lastSeenAt = now;
        active = true;
    }

    void noLongerVisible()
    {
        active = false;
    }

    public int getWorld() { return world; }
    public WorldPoint getPoint() { return point; }
    public String getLocation() { return location; }
    public int getTier() { return tier; }
    public long getFirstSeenAt() { return firstSeenAt; }
    public long getLastSeenAt() { return lastSeenAt; }
    public boolean isActive() { return active; }
}
