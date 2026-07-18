package com.adventureplanner;

public enum PlannerLanguage
{
    DUTCH("Nederlands"),
    ENGLISH("English");

    private final String label;

    PlannerLanguage(String label)
    {
        this.label = label;
    }

    public boolean isEnglish()
    {
        return this == ENGLISH;
    }

    @Override
    public String toString()
    {
        return label;
    }
}
