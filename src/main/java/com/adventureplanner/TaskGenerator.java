package com.adventureplanner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import net.runelite.api.Skill;

public final class TaskGenerator
{
    private final Random random;

    public TaskGenerator(Random random)
    {
        this.random = random;
    }

    public GeneratedTask generate(Map<Skill, Integer> levels, List<Skill> recent, int chunkSize)
    {
        return generate(levels, recent, chunkSize, Collections.emptySet());
    }

    public GeneratedTask generate(Map<Skill, Integer> levels, List<Skill> recent, int chunkSize,
        Set<Skill> blocked)
    {
        List<SkillGoal> diaryCandidates = new ArrayList<>();
        for (SkillGoal goal : DiaryGoalCatalog.goals())
        {
            if (!blocked.contains(goal.getSkill())
                && level(levels, goal.getSkill()) < goal.getTargetLevel())
            {
                diaryCandidates.add(goal);
            }
        }

        if (!diaryCandidates.isEmpty())
        {
            SkillGoal selected = selectDiaryGoal(diaryCandidates, levels, recent);
            int current = level(levels, selected.getSkill());
            int target = Math.min(selected.getTargetLevel(), current + chunkSize);
            return new GeneratedTask(selected.getSkill(), current, target, "Diary", selected.getReason());
        }

        List<Skill> candidates = new ArrayList<>();
        for (Skill skill : Skill.values())
        {
            if (skill != Skill.OVERALL && !blocked.contains(skill)
                && level(levels, skill) < 99 && !recent.contains(skill))
            {
                candidates.add(skill);
            }
        }
        if (candidates.isEmpty())
        {
            for (Skill skill : Skill.values())
            {
                if (skill != Skill.OVERALL && !blocked.contains(skill) && level(levels, skill) < 99)
                {
                    candidates.add(skill);
                }
            }
        }
        if (candidates.isEmpty())
        {
            return null;
        }

        Skill selected = candidates.get(random.nextInt(candidates.size()));
        int current = level(levels, selected);
        return new GeneratedTask(selected, current, Math.min(99, current + chunkSize), "Maxing", "op weg naar level 99");
    }

    private SkillGoal selectDiaryGoal(List<SkillGoal> goals, Map<Skill, Integer> levels, List<Skill> recent)
    {
        List<SkillGoal> fresh = new ArrayList<>();
        for (SkillGoal goal : goals)
        {
            if (!recent.contains(goal.getSkill())) fresh.add(goal);
        }
        List<SkillGoal> pool = fresh.isEmpty() ? goals : fresh;
        Collections.sort(pool, Comparator.comparingInt(g -> g.getTargetLevel() - level(levels, g.getSkill())));
        int variedTop = Math.min(5, pool.size());
        return pool.get(random.nextInt(variedTop));
    }

    private static int level(Map<Skill, Integer> levels, Skill skill)
    {
        Integer value = levels.get(skill);
        return value == null ? 1 : value;
    }
}
