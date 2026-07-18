package com.adventureplanner;

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.runelite.api.Skill;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ResourceAwareTrainingTest
{
    @Test
    public void magicLongbowsAreCappedToActualBankAmount()
    {
        Map<Skill, Integer> levels = levelsAt(99);
        levels.put(Skill.FLETCHING, 85);
        Map<String, Integer> supplies = Collections.singletonMap("Magic logs", 73);

        List<BankTaskCandidate> tasks = LevelTrainingCatalog.candidates(
            levels, Collections.emptyList(), 90, new Random(1), supplies);
        BankTaskCandidate longbows = tasks.stream()
            .filter(task -> task.getTitle().contains("magic longbows (u)"))
            .findFirst().orElse(null);

        assertNotNull(longbows);
        assertEquals(73, GeneratedTask.activity(longbows.getSkill(),
            longbows.getTitle(), longbows.getDetail()).getTargetAmount());
    }

    @Test
    public void impossibleFletchingMethodsAreNotOffered()
    {
        Map<Skill, Integer> levels = levelsAt(99);
        levels.put(Skill.FLETCHING, 85);

        List<BankTaskCandidate> tasks = LevelTrainingCatalog.candidates(
            levels, Collections.emptyList(), 90, new Random(1), Collections.emptyMap());

        assertFalse(tasks.stream().anyMatch(task -> task.getSkill() == Skill.FLETCHING));
    }

    @Test
    public void missingMagicLogsCreateExactLongbowRefillTask()
    {
        Map<Skill, Integer> levels = levelsAt(99);
        levels.put(Skill.FLETCHING, 85);

        BankTaskCandidate task = SupplyChainCatalog.prerequisiteFor(
            Skill.FLETCHING, levels, Collections.emptySet(), 30, Collections.emptyMap());

        assertNotNull(task);
        assertTrue(task.getTitle().contains("magic logs"));
        assertTrue(task.getTitle().contains("magic longbows (u)"));
    }

    @Test
    public void ensouledHeadTaskIsLimitedByHeadsAndExactRunes()
    {
        TrainingMethod method = new TrainingMethod(Skill.PRAYER, 90, "Reanimate",
            "ensouled dragon heads", 300, "");
        Map<String, Integer> supplies = new HashMap<>();
        supplies.put("Ensouled dragon head", 8);
        supplies.put("Blood rune", 28);
        supplies.put("Nature rune", 40);
        supplies.put("Soul rune", 20);

        assertEquals(7, TrainingSupplyCatalog.availableUnits(method, supplies));
    }

    @Test
    public void profitableEndProductGetsGeMargin()
    {
        BankTaskCandidate candidate = new BankTaskCandidate(Skill.FLETCHING,
            "Fletch 73 magic longbows (u)", "Gebruik magic logs");
        ItemPriceProvider prices = name -> {
            if ("Magic longbow (u)".equals(name)) return 1600;
            if ("Magic logs".equals(name)) return 1100;
            return 0;
        };

        BankTaskCandidate selected = ProfitAwareTaskSelector.select(
            Collections.singletonList(candidate), new Random(1), prices);

        assertTrue(selected.getDetail().contains("GE-winst: +500 gp"));
    }

    private static Map<Skill, Integer> levelsAt(int level)
    {
        Map<Skill, Integer> levels = new EnumMap<>(Skill.class);
        for (Skill skill : Skill.values()) levels.put(skill, level);
        return levels;
    }
}
