package com.adventureplanner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Random;
import net.runelite.api.Skill;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class MiningConcreteTaskTest
{
    @Test
    public void miningCandidateAlwaysHasMethodAndAmount()
    {
        Map<Skill, Integer> levels = levelsAt(99);
        levels.put(Skill.MINING, 50);

        BankTaskCandidate task = LevelTrainingCatalog.randomCandidateFor(
            Skill.MINING, levels, 60, new Random(1));

        assertNotNull(task);
        assertTrue(task.getTitle().startsWith("Mine "));
        assertFalse(task.getTitle().contains("minuten"));
        assertTrue(GeneratedTask.activity(Skill.MINING, task.getTitle(), task.getDetail())
            .getTargetAmount() > 0);
    }

    @Test
    public void diaryMiningGoalNeverUsesGenericTimedTask()
    {
        Map<Skill, Integer> levels = levelsAt(99);
        levels.put(Skill.MINING, 50);
        GeneratedTask task = new SmartTaskGenerator(new Random(2)).generate(
            levels, new ArrayList<>(), 2, 60, Collections.emptyMap(),
            Collections.emptySet(), PlannerFocus.DIARIES, "");

        assertNotNull(task);
        assertFalse(task.getTitle().startsWith("Train Mining"));
        assertTrue(task.getTargetAmount() > 0);
    }

    @Test
    public void recognizesOldGenericMiningTaskForReplacement()
    {
        GeneratedTask oldTask = GeneratedTask.shortProgression(
            new GeneratedTask(Skill.MINING, 50, 52, "Diary", "elite diary"), 90);

        assertTrue(AdventurePlannerPanel.isGenericSkillTask(oldTask));
    }

    private static Map<Skill, Integer> levelsAt(int level)
    {
        Map<Skill, Integer> levels = new EnumMap<>(Skill.class);
        for (Skill skill : Skill.values()) levels.put(skill, level);
        return levels;
    }
}
