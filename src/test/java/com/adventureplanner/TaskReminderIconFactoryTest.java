package com.adventureplanner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TaskReminderIconFactoryTest
{
    @Test
    public void everyDefaultChecklistTaskHasAnItemLogo()
    {
        for (PlannerTask task : TaskCatalog.defaults())
        {
            if (!task.isRecurring())
            {
                assertTrue(task.getId(), TaskReminderIconFactory.itemId(task.getId()) > 0);
            }
        }
    }

    @Test
    public void customTasksUseTheFallbackBadge()
    {
        assertEquals(-1, TaskReminderIconFactory.itemId("custom_example"));
    }
}
