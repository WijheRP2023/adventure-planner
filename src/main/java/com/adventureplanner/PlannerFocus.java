package com.adventureplanner;

public enum PlannerFocus
{
    BALANCED("Gebalanceerd"),
    DIARIES("Achievement Diaries"),
    COMBAT("Combat"),
    QUEST("Quest");

    private final String label;

    PlannerFocus(String label)
    {
        this.label = label;
    }

    @Override
    public String toString()
    {
        return label;
    }
}
