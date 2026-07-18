package com.adventureplanner;

public enum RunType
{
    HERB("HERBS"),
    TREE("TREES"),
    BIRDHOUSE("BIRDS"),
    SEAWEED("SEA");

    private final String label;

    RunType(String label)
    {
        this.label = label;
    }

    public String getLabel()
    {
        return label;
    }
}
