package com.adventureplanner;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class RunTimerDisplayTest
{
    @Test
    public void progressUsesElapsedPartOfTimer()
    {
        assertEquals(0, AdventurePlannerPanel.runProgress(0L, 10_000L, 5_000L));
        assertEquals(0, AdventurePlannerPanel.runProgress(1_000L, 11_000L, 500L));
        assertEquals(50, AdventurePlannerPanel.runProgress(1_000L, 11_000L, 6_000L));
        assertEquals(100, AdventurePlannerPanel.runProgress(1_000L, 11_000L, 11_000L));
    }

    @Test
    public void specialTreeTimersUseTheirOwnGrowthDurations()
    {
        assertEquals(3_840,
            RecurringActivityManager.treeCooldownMinutes("spirit sapling -> spirit tree patch"));
        assertEquals(6_400,
            RecurringActivityManager.treeCooldownMinutes("redwood sapling -> redwood tree patch"));
        assertEquals(7_680,
            RecurringActivityManager.treeCooldownMinutes("mahogany sapling -> hardwood patch"));
    }
}
