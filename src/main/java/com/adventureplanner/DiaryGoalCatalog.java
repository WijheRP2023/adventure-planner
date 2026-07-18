package com.adventureplanner;

import java.util.ArrayList;
import java.util.List;
import net.runelite.api.Skill;

/** Milestones used to keep diary progress ahead of the maxing phase. */
public final class DiaryGoalCatalog
{
    private DiaryGoalCatalog() {}

    public static List<SkillGoal> goals()
    {
        List<SkillGoal> goals = new ArrayList<>();
        add(goals, Skill.AGILITY, 90, "elite diaries");
        add(goals, Skill.ATTACK, 78, "elite diaries");
        add(goals, Skill.CONSTRUCTION, 90, "elite diaries");
        add(goals, Skill.COOKING, 95, "elite diaries");
        add(goals, Skill.CRAFTING, 85, "elite diaries");
        add(goals, Skill.DEFENCE, 70, "elite diaries");
        add(goals, Skill.FARMING, 91, "elite diaries");
        add(goals, Skill.FIREMAKING, 85, "elite diaries");
        add(goals, Skill.FISHING, 96, "elite diaries");
        add(goals, Skill.FLETCHING, 95, "elite diaries");
        add(goals, Skill.HERBLORE, 90, "elite diaries");
        add(goals, Skill.HITPOINTS, 70, "elite diaries");
        add(goals, Skill.HUNTER, 70, "elite diaries");
        add(goals, Skill.MAGIC, 96, "elite diaries");
        add(goals, Skill.MINING, 85, "elite diaries");
        add(goals, Skill.PRAYER, 85, "elite diaries");
        add(goals, Skill.RANGED, 70, "elite diaries");
        add(goals, Skill.RUNECRAFT, 91, "elite diaries");
        add(goals, Skill.SLAYER, 95, "elite diaries");
        add(goals, Skill.SMITHING, 91, "elite diaries");
        add(goals, Skill.STRENGTH, 76, "elite diaries");
        add(goals, Skill.THIEVING, 91, "elite diaries");
        add(goals, Skill.WOODCUTTING, 90, "elite diaries");
        return goals;
    }

    private static void add(List<SkillGoal> goals, Skill skill, int level, String reason)
    {
        goals.add(new SkillGoal(skill, level, reason));
    }
}
