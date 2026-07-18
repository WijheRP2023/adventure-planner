package com.adventureplanner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Collections;
import net.runelite.api.Skill;
import org.junit.Test;

public class TaskGeneratorTest
{
    @Test
    public void diaryGoalsComeBeforeMaxing()
    {
        Map<Skill, Integer> levels = levelsAt(70);
        GeneratedTask task = new TaskGenerator(new Random(1)).generate(levels, new ArrayList<>(), 2);
        assertNotNull(task);
        assertEquals("Diary", task.getPhase());
        assertTrue(task.getTargetLevel() <= task.getStartLevel() + 2);
    }

    @Test
    public void avoidsRecentSkill()
    {
        Map<Skill, Integer> levels = levelsAt(99);
        levels.put(Skill.AGILITY, 98);
        levels.put(Skill.MINING, 98);
        List<Skill> recent = new ArrayList<>();
        recent.add(Skill.AGILITY);
        GeneratedTask task = new TaskGenerator(new Random(1)).generate(levels, recent, 2);
        assertEquals(Skill.MINING, task.getSkill());
        assertEquals("Maxing", task.getPhase());
    }

    @Test
    public void neverSelectsBlockedSkill()
    {
        Map<Skill, Integer> levels = levelsAt(99);
        levels.put(Skill.MINING, 50);
        GeneratedTask task = new TaskGenerator(new Random(1)).generate(
            levels, new ArrayList<>(), 2, Collections.singleton(Skill.MINING));
        assertEquals(null, task);
    }

    private static Map<Skill, Integer> levelsAt(int level)
    {
        Map<Skill, Integer> levels = new EnumMap<>(Skill.class);
        for (Skill skill : Skill.values()) levels.put(skill, level);
        return levels;
    }
}
