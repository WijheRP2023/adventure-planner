package com.adventureplanner;

import net.runelite.api.Skill;

public final class SkillActivity
{
    private final Skill skill;
    private final int minimumLevel;
    private final String title;
    private final String detail;

    public SkillActivity(Skill skill, int minimumLevel, String title, String detail)
    {
        this.skill = skill;
        this.minimumLevel = minimumLevel;
        this.title = title;
        this.detail = detail;
    }

    public Skill getSkill() { return skill; }
    public int getMinimumLevel() { return minimumLevel; }
    public String title(int minutes) { return title.replace("{minutes}", Integer.toString(minutes)); }
    public String getDetail() { return detail; }
}
