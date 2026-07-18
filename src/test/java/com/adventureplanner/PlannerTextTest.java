package com.adventureplanner;

import static org.junit.Assert.assertEquals;

import net.runelite.api.Skill;
import org.junit.Test;

public class PlannerTextTest
{
    @Test
    public void dutchIsReturnedUnchanged()
    {
        assertEquals("Nog geen taak gekozen",
            PlannerText.translate(PlannerLanguage.DUTCH, "Nog geen taak gekozen"));
    }

    @Test
    public void fixedSidebarTextIsTranslated()
    {
        assertEquals("No task selected",
            PlannerText.translate(PlannerLanguage.ENGLISH, "Nog geen taak gekozen"));
        assertEquals("Zaff: collect battlestaves",
            PlannerText.translate(PlannerLanguage.ENGLISH, "Zaff: battlestaves ophalen"));
    }

    @Test
    public void generatedTaskTextIsTranslatedWithoutChangingStoredTask()
    {
        assertEquals("Chop 100 yew logs for Fletching",
            PlannerText.translate(PlannerLanguage.ENGLISH,
                "Hak 100 yew logs voor Fletching"));
        assertEquals("Work on Desert Treasure for 30 minutes",
            PlannerText.translate(PlannerLanguage.ENGLISH,
                "Werk 30 minuten aan Desert Treasure"));
    }
    @Test
    public void activeAutomaticTaskUsesConfiguredDisplayLanguage()
    {
        GeneratedTask task = GeneratedTask.activity(Skill.WOODCUTTING,
            "Hak 100 yew logs voor Fletching",
            "Voorraadketen: verzamel exact deze logs; daarna kan de planner ze verwerken");

        assertEquals("Supply/activity", task.getDisplayPhase(PlannerLanguage.ENGLISH));
        assertEquals("Chop 100 yew logs for Fletching",
            task.getDisplayTitle(PlannerLanguage.ENGLISH));
        assertEquals("Supply chain: collect these exact logs; the planner can process them next",
            task.getDisplayReason(PlannerLanguage.ENGLISH));
        assertEquals("Hak 100 yew logs voor Fletching",
            task.getDisplayTitle(PlannerLanguage.DUTCH));
    }

}
