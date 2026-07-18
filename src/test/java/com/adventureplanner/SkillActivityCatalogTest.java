package com.adventureplanner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.runelite.api.Skill;
import org.junit.Test;

public class SkillActivityCatalogTest
{
    @Test
    public void activityCatalogDoesNotReintroduceGenericTrainingTitles()
    {
        String[] genericTitles = {
            "Train Attack", "Train Strength", "Train Defence", "Train Ranged",
            "Train Prayer", "Kook voorraad", "Fletch 30 minuten",
            "Crafting-voorraad", "een rooftop course"
        };
        for (SkillActivity activity : SkillActivityCatalog.activities())
        {
            for (String generic : genericTitles)
            {
                assertFalse(activity.title(30), activity.title(30).contains(generic));
            }
        }
    }

    @Test
    public void shootingStarsUnlockAtTenMining()
    {
        Map<Skill, Integer> levels = new EnumMap<>(Skill.class);
        levels.put(Skill.MINING, 9);
        assertFalse(hasTitle(levels, "Shooting Star"));
        levels.put(Skill.MINING, 10);
        assertTrue(hasTitle(levels, "Shooting Star"));
    }

    @Test
    public void temporossUnlocksAtThirtyFiveFishing()
    {
        Map<Skill, Integer> levels = new EnumMap<>(Skill.class);
        levels.put(Skill.FISHING, 34);
        assertFalse(hasTitle(levels, "Tempoross"));
        levels.put(Skill.FISHING, 35);
        assertTrue(hasTitle(levels, "Tempoross"));
    }

    private static boolean hasTitle(Map<Skill, Integer> levels, String text)
    {
        return SkillActivityCatalog.candidates(levels, new ArrayList<>(), 30).stream()
            .anyMatch(task -> task.getTitle().contains(text));
    }
}
