package com.adventureplanner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Map;
import net.runelite.api.Skill;
import org.junit.Test;

public class FarmingSupplyTest
{
    @Test
    public void selectsHighestSeedThePlayerCanUse()
    {
        Map<String, Integer> supplies = new LinkedHashMap<>();
        supplies.put("Ranarr seed", 12);
        supplies.put("Torstol seed", 4);

        assertEquals("Ranarr seed", SmartTaskGenerator.bestHerbSeed(supplies, 70));
        assertEquals("Torstol seed", SmartTaskGenerator.bestHerbSeed(supplies, 85));
    }

    @Test
    public void includesSaplingsAsTreeRunSupplies()
    {
        Map<String, Integer> supplies = new LinkedHashMap<>();
        supplies.put("Yew sapling", 5);

        assertEquals("Yew sapling", SmartTaskGenerator.bestTreeSeed(supplies, 60));
    }

    @Test
    public void seedBoxGoalUsesTitheSeedForLevel()
    {
        Map<Skill, Integer> levels = new EnumMap<>(Skill.class);
        levels.put(Skill.FARMING, 60);
        BankTaskCandidate goal = SmartTaskGenerator.seedBoxGoal(
            levels, new ArrayList<>(), 30, new LinkedHashMap<>());

        assertTrue(goal.getDetail().contains("Bologano seeds"));
    }

    @Test
    public void ownedSeedBoxRemovesAcquisitionGoal()
    {
        Map<Skill, Integer> levels = new EnumMap<>(Skill.class);
        levels.put(Skill.FARMING, 80);
        Map<String, Integer> supplies = new LinkedHashMap<>();
        supplies.put("Seed box", 1);

        assertNull(SmartTaskGenerator.seedBoxGoal(levels, new ArrayList<>(), 30, supplies));
    }
}
