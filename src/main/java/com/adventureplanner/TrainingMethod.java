package com.adventureplanner;

import net.runelite.api.Skill;

public final class TrainingMethod
{
    private final Skill skill;
    private final int minimumLevel;
    private final String verb;
    private final String target;
    private final int unitsPerHour;
    private final String detail;

    public TrainingMethod(Skill skill, int minimumLevel, String verb, String target,
        int unitsPerHour, String detail)
    {
        this.skill = skill;
        this.minimumLevel = minimumLevel;
        this.verb = verb;
        this.target = target;
        this.unitsPerHour = unitsPerHour;
        this.detail = detail;
    }

    public Skill getSkill() { return skill; }
    public int getMinimumLevel() { return minimumLevel; }
    public String getVerb() { return verb; }
    public String getTarget() { return target; }
    public int getUnitsPerHour() { return unitsPerHour; }
    public String getDetail() { return detail; }
}
