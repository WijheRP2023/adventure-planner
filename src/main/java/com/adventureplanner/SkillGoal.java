package com.adventureplanner;

import net.runelite.api.Skill;

public final class SkillGoal
{
    private final Skill skill;
    private final int targetLevel;
    private final String reason;

    public SkillGoal(Skill skill, int targetLevel, String reason)
    {
        this.skill = skill;
        this.targetLevel = targetLevel;
        this.reason = reason;
    }

    public Skill getSkill() { return skill; }
    public int getTargetLevel() { return targetLevel; }
    public String getReason() { return reason; }
}
