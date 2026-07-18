package com.adventureplanner;

public enum TaskCadence
{
    DAILY("Dagelijks"),
    WEEKLY("Wekelijks");

    private final String label;

    TaskCadence(String label)
    {
        this.label = label;
    }

    public String getLabel()
    {
        return label;
    }
}
