package com.adventureplanner;

public final class HunterRumour
{
    private final String id;
    private final String name;
    private final String location;
    private final String requirements;

    public HunterRumour(String id, String name, String location, String requirements)
    {
        this.id = id;
        this.name = name;
        this.location = location;
        this.requirements = requirements;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getLocation() { return location; }
    public String getRequirements() { return requirements; }
}
