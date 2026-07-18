package com.adventureplanner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.runelite.api.Skill;
import org.junit.Test;

public class BankTaskCatalogTest
{
    @Test
    public void bankTasksAreCappedAtFiftyActions()
    {
        Map<String, Integer> bank = new LinkedHashMap<>();
        bank.put("Raw lobster", 800);
        Map<Skill, Integer> levels = new EnumMap<>(Skill.class);
        levels.put(Skill.COOKING, 80);

        List<BankTaskCandidate> tasks = BankTaskCatalog.candidates(bank, levels);
        assertEquals(1, tasks.size());
        assertTrue(tasks.get(0).getTitle().contains("50×"));
    }

    @Test
    public void wineRequiresBothIngredients()
    {
        Map<String, Integer> bank = new LinkedHashMap<>();
        bank.put("Grapes", 40);
        bank.put("Jug of water", 30);
        Map<Skill, Integer> levels = new EnumMap<>(Skill.class);
        levels.put(Skill.COOKING, 35);

        List<BankTaskCandidate> tasks = BankTaskCatalog.candidates(bank, levels);
        assertTrue(tasks.stream().anyMatch(task -> task.getTitle().contains("30× jug of wine")));
    }

    @Test
    public void ensouledHeadsRespectMagicRequirement()
    {
        Map<String, Integer> bank = new LinkedHashMap<>();
        bank.put("Ensouled abyssal head", 20);
        bank.put("Nature rune", 80);
        bank.put("Soul rune", 80);
        bank.put("Blood rune", 40);
        Map<Skill, Integer> levels = new EnumMap<>(Skill.class);
        levels.put(Skill.PRAYER, 70);
        levels.put(Skill.MAGIC, 89);
        assertTrue(BankTaskCatalog.candidates(bank, levels).isEmpty());

        levels.put(Skill.MAGIC, 90);
        assertTrue(BankTaskCatalog.candidates(bank, levels).stream()
            .anyMatch(task -> task.getTitle().contains("Ensouled abyssal head")));
    }

    @Test
    public void ensouledTaskListsTotalRunes()
    {
        Map<String, Integer> bank = new LinkedHashMap<>();
        bank.put("Ensouled goblin head", 10);
        bank.put("Body rune", 40);
        bank.put("Nature rune", 20);
        Map<Skill, Integer> levels = new EnumMap<>(Skill.class);
        levels.put(Skill.PRAYER, 30);
        levels.put(Skill.MAGIC, 16);

        BankTaskCandidate task = BankTaskCatalog.candidates(bank, levels).get(0);
        assertTrue(task.getDetail().contains("40 body, 20 nature runes"));
    }
}
