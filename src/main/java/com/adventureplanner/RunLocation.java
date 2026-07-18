package com.adventureplanner;

import net.runelite.api.coords.WorldPoint;

public final class RunLocation
{
    private final String id;
    private final RunType type;
    private final String name;
    private final WorldPoint point;
    private final String requirements;
    private final String teleport;

    public RunLocation(String id, RunType type, String name, WorldPoint point,
        String requirements, String teleport)
    {
        this.id = id;
        this.type = type;
        this.name = name;
        this.point = point;
        this.requirements = requirements;
        this.teleport = teleport;
    }

    public String getId() { return id; }
    public RunType getType() { return type; }
    public String getName() { return name; }
    public WorldPoint getPoint() { return point; }
    public String getRequirements() { return requirements; }
    public String getTeleport() { return teleport; }
    public String timerId() { return "run.location." + id; }
}
