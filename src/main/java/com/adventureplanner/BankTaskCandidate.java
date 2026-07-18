package com.adventureplanner;

import net.runelite.api.Skill;

public final class BankTaskCandidate
{
    private final Skill skill;
    private final String title;
    private final String detail;

    public BankTaskCandidate(Skill skill, String title, String detail)
    {
        this.skill = skill;
        this.title = title;
        this.detail = detail;
    }

    public Skill getSkill() { return skill; }
    public String getTitle() { return title; }
    public String getDetail() { return detail; }
}
