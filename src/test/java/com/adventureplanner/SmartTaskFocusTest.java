package com.adventureplanner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Random;
import net.runelite.api.Skill;
import org.junit.Test;

public class SmartTaskFocusTest
{
    @Test
    public void questFocusUsesConfiguredQuest()
    {
        GeneratedTask task = generator().generate(levelsAt(50), new ArrayList<>(), 2, 30,
            Collections.emptyMap(), Collections.emptySet(), PlannerFocus.QUEST,
            "Desert Treasure II");
        assertEquals(Skill.OVERALL, task.getSkill());
        assertTrue(task.getTitle().contains("Desert Treasure II"));
    }

    @Test
    public void combatFocusOnlyReturnsCombatSkill()
    {
        GeneratedTask task = generator().generate(levelsAt(50), new ArrayList<>(), 2, 30,
            Collections.emptyMap(), Collections.emptySet(), PlannerFocus.COMBAT, "");
        assertTrue(isCombat(task.getSkill()));
    }

    @Test
    public void slayerTaskNeverLocksToExistingAssignment()
    {
        Map<Skill, Integer> levels = levelsAt(99);
        levels.put(Skill.SLAYER, 50);
        GeneratedTask task = generator().generate(levels, new ArrayList<>(), 2, 30,
            Collections.emptyMap(), Collections.emptySet(), PlannerFocus.COMBAT, "");
        assertEquals(Skill.SLAYER, task.getSkill());
        assertTrue(task.getTitle().contains("maximaal 30 minuten"));
        assertTrue(task.getReason().contains("altijd wisselen"));
    }

    private static SmartTaskGenerator generator()
    {
        return new SmartTaskGenerator(new Random()
        {
            @Override public int nextInt(int bound) { return 0; }
        });
    }

    private static boolean isCombat(Skill skill)
    {
        return skill == Skill.ATTACK || skill == Skill.STRENGTH || skill == Skill.DEFENCE
            || skill == Skill.HITPOINTS || skill == Skill.RANGED || skill == Skill.PRAYER
            || skill == Skill.MAGIC || skill == Skill.SLAYER;
    }

    private static Map<Skill, Integer> levelsAt(int level)
    {
        Map<Skill, Integer> levels = new EnumMap<>(Skill.class);
        for (Skill skill : Skill.values()) levels.put(skill, level);
        return levels;
    }
}
