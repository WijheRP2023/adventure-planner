package com.adventureplanner;

import net.runelite.api.Skill;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AdventurePlannerAutoAdvanceTest
{
    private static final long NOW = 10_000L;

    @Test
    public void advancesWhenTimerExpires()
    {
        GeneratedTask task = GeneratedTask.activity(
            Skill.CONSTRUCTION, "Train Construction 30 minuten", "Bouw meubels");

        assertTrue(AdventurePlannerPanel.shouldAdvanceTask(task, NOW, 0, NOW));
    }

    @Test
    public void advancesWhenTargetAmountIsReached()
    {
        GeneratedTask task = GeneratedTask.activity(
            Skill.WOODCUTTING, "Hak 100 yew logs", "Hak yew trees");

        assertTrue(AdventurePlannerPanel.shouldAdvanceTask(task, NOW + 60_000L, 100, NOW));
    }

    @Test
    public void keepsCurrentTaskWhileTimeAndAmountRemain()
    {
        GeneratedTask task = GeneratedTask.activity(
            Skill.FISHING, "Vang 75 monkfish", "Vis bij de Piscatoris Fishing Colony");

        assertFalse(AdventurePlannerPanel.shouldAdvanceTask(task, NOW + 60_000L, 74, NOW));
    }

    @Test
    public void ignoresMissingTask()
    {
        assertFalse(AdventurePlannerPanel.shouldAdvanceTask(null, NOW, 100, NOW));
    }
}
