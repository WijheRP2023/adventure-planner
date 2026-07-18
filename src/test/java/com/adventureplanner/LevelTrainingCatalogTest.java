package com.adventureplanner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Map;
import java.util.Random;
import net.runelite.api.Skill;
import org.junit.Test;

public class LevelTrainingCatalogTest
{
    @Test
    public void catalogProvidesAtLeastFiveHundredQuantityVariations()
    {
        assertTrue(LevelTrainingCatalog.estimatedVariantCount() >= 500);
    }

    @Test
    public void sharksOnlyAppearAtFishingSeventySix()
    {
        Map<Skill, Integer> levels = new EnumMap<>(Skill.class);
        levels.put(Skill.FISHING, 75);
        assertTrue(noTitle(levels, "sharks"));
        levels.put(Skill.FISHING, 76);
        assertTrue(!noTitle(levels, "sharks"));
    }

    @Test
    public void meleeMonsterFollowsStats()
    {
        Map<Skill, Integer> levels = new EnumMap<>(Skill.class);
        levels.put(Skill.ATTACK, 10);
        levels.put(Skill.STRENGTH, 10);
        levels.put(Skill.DEFENCE, 10);
        assertEquals("cows", LevelTrainingCatalog.meleeMonster(levels));
        levels.put(Skill.ATTACK, 70);
        levels.put(Skill.STRENGTH, 70);
        levels.put(Skill.DEFENCE, 70);
        assertEquals("Scurrius", LevelTrainingCatalog.meleeMonster(levels));
    }

    @Test
    public void everySkillHasMultipleTrainingChoices()
    {
        Map<Skill, Integer> levels = new EnumMap<>(Skill.class);
        for (Skill skill : Skill.values()) levels.put(skill, 86);

        java.util.List<BankTaskCandidate> choices = new ArrayList<>();
        choices.addAll(LevelTrainingCatalog.candidates(
            levels, new ArrayList<>(), 60, new Random(2)));
        choices.addAll(SkillActivityCatalog.candidates(
            levels, new ArrayList<>(), 60));

        for (Skill skill : Skill.values())
        {
            if (skill == Skill.OVERALL) continue;
            long count = choices.stream().filter(choice -> choice.getSkill() == skill).count();
            assertTrue(skill.getName() + " heeft minder dan twee trainingsmethoden", count >= 2);
        }
    }

    private static boolean noTitle(Map<Skill, Integer> levels, String text)
    {
        return LevelTrainingCatalog.candidates(levels, new ArrayList<>(), 30, new Random(1))
            .stream().noneMatch(task -> task.getTitle().contains(text));
    }
    @Test
    public void highLevelConstructionHasSeveralConcreteMethods()
    {
        Map<Skill, Integer> levels = new EnumMap<>(Skill.class);
        levels.put(Skill.CONSTRUCTION, 86);
        long methods = LevelTrainingCatalog.candidates(
            levels, new ArrayList<>(), 30, new Random(1)).stream()
            .filter(task -> task.getSkill() == Skill.CONSTRUCTION)
            .count();
        assertTrue(methods >= 4);
    }

    @Test
    public void yewLogsUnlockAtWoodcuttingSixty()
    {
        Map<Skill, Integer> levels = new EnumMap<>(Skill.class);
        levels.put(Skill.WOODCUTTING, 59);
        assertTrue(noTitle(levels, "yew logs"));
        levels.put(Skill.WOODCUTTING, 60);
        assertTrue(!noTitle(levels, "yew logs"));
    }

    @Test
    public void highLevelTasksDoNotUseOldGenericTargets()
    {
        Map<Skill, Integer> levels = new EnumMap<>(Skill.class);
        for (Skill skill : Skill.values()) levels.put(skill, 86);
        String[] genericTargets = {
            "passende melee-monsters", "passende combat-monsters",
            "monsters met Ranged", "raw food", "Forestry-logs",
            "unstrung bows", "Crafting-items", "metalen items",
            "grimy herbs", "passende targets", "Slayer-targets"
        };
        for (BankTaskCandidate task : LevelTrainingCatalog.candidates(
            levels, new ArrayList<>(), 60, new Random(4)))
        {
            for (String generic : genericTargets)
            {
                assertTrue(task.getTitle(), !task.getTitle().contains(generic));
            }
        }
    }
}
