package com.adventureplanner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import net.runelite.api.Skill;
import org.junit.Test;

public class AdventurePlannerConfigTest
{
    @Test
    public void allSkillsAreEnabledByDefault()
    {
        AdventurePlannerConfig config = new AdventurePlannerConfig() {};
        assertTrue(config.language() == PlannerLanguage.DUTCH);
        assertTrue(PlannerConfigSelections.blockedSkills(config).isEmpty());
    }

    @Test
    public void uncheckedSkillBecomesBlocked()
    {
        AdventurePlannerConfig config = new AdventurePlannerConfig()
        {
            @Override
            public boolean includeMining()
            {
                return false;
            }
        };

        assertTrue(PlannerConfigSelections.blockedSkills(config).contains(Skill.MINING));
        assertFalse(PlannerConfigSelections.blockedSkills(config).contains(Skill.ATTACK));
    }

    @Test
    public void allDailyTasksAreVisibleByDefault()
    {
        AdventurePlannerConfig config = new AdventurePlannerConfig() {};
        assertTrue(PlannerConfigSelections.hiddenDailyTasks(config).isEmpty());
    }

    @Test
    public void uncheckedDailyTaskBecomesHidden()
    {
        AdventurePlannerConfig config = new AdventurePlannerConfig()
        {
            @Override
            public boolean showBirdhouses()
            {
                return false;
            }
        };

        assertTrue(PlannerConfigSelections.hiddenDailyTasks(config).contains(DailyTaskOption.BIRDHOUSES));
        assertFalse(PlannerConfigSelections.hiddenDailyTasks(config).contains(DailyTaskOption.HERB_RUN));
    }
    @Test
    public void allWeeklyTasksAndRemindersAreEnabledByDefault()
    {
        AdventurePlannerConfig config = new AdventurePlannerConfig() {};
        assertTrue(PlannerConfigSelections.hiddenWeeklyTasks(config).isEmpty());
        assertTrue(config.showTaskReminderInfoBoxes());
    }

    @Test
    public void uncheckedWeeklyTaskBecomesHidden()
    {
        AdventurePlannerConfig config = new AdventurePlannerConfig()
        {
            @Override
            public boolean showWeeklyBoss()
            {
                return false;
            }
        };

        assertTrue(PlannerConfigSelections.hiddenWeeklyTasks(config).contains(WeeklyTaskOption.BOSS));
        assertFalse(PlannerConfigSelections.hiddenWeeklyTasks(config).contains(WeeklyTaskOption.TEARS));
    }
}
