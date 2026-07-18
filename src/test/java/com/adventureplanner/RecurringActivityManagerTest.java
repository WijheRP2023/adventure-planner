package com.adventureplanner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class RecurringActivityManagerTest
{
    @Test
    public void recognizesHerbAndTreePlantingTargets()
    {
        assertEquals(RunType.HERB,
            RecurringActivityManager.classifyPlantTarget("ranarr seed -> herb patch"));
        assertEquals(RunType.TREE,
            RecurringActivityManager.classifyPlantTarget("yew sapling -> tree patch"));
        assertEquals(RunType.SEAWEED,
            RecurringActivityManager.classifyPlantTarget("seaweed spore -> seaweed patch"));
        assertNull(RecurringActivityManager.classifyPlantTarget("compost -> herb patch"));
    }

    @Test
    public void derivesTreeCooldownFromSapling()
    {
        assertEquals(200, RecurringActivityManager.treeCooldownMinutes("oak sapling -> tree patch"));
        assertEquals(400, RecurringActivityManager.treeCooldownMinutes("yew sapling -> tree patch"));
        assertEquals(960, RecurringActivityManager.treeCooldownMinutes("palm sapling -> fruit tree patch"));
    }
}
