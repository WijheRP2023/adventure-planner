package com.adventureplanner;

import static org.junit.Assert.assertEquals;

import java.util.LinkedHashMap;
import java.util.Map;
import net.runelite.api.Skill;
import org.junit.Test;

public class GeneratedTaskProgressTest
{
    @Test
    public void quantityTaskExposesItsTarget()
    {
        GeneratedTask task = GeneratedTask.activity(
            Skill.WOODCUTTING, "Hak 405 logs", "Kies logs voor je level");
        assertEquals(405, task.getTargetAmount());
    }

    @Test
    public void timedTaskDoesNotPretendMinutesAreItems()
    {
        GeneratedTask task = GeneratedTask.activity(
            Skill.WOODCUTTING, "Doe 60 minuten Forestry", "Doe mee aan events");
        assertEquals(0, task.getTargetAmount());
    }

    @Test
    public void levelGoalIsNotAQuantityTask()
    {
        GeneratedTask task = new GeneratedTask(
            Skill.WOODCUTTING, 84, 86, "Diary", "Richting diary");
        assertEquals(0, task.getTargetAmount());
    }

    @Test
    public void existingLogsSeedTheVisibleProgress()
    {
        GeneratedTask task = GeneratedTask.activity(
            Skill.WOODCUTTING, "Hak 405 logs", "Kies logs voor je level");
        Map<String, Integer> inventory = new LinkedHashMap<>();
        inventory.put("Maple logs", 3);
        inventory.put("Coins", 1000);
        assertEquals(3, task.startingInventoryProgress(inventory));
    }
}
