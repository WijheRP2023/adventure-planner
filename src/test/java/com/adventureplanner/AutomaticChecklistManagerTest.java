package com.adventureplanner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class AutomaticChecklistManagerTest
{
    @Test
    public void recognizesSupportedMinigameCompletions()
    {
        assertTrue(AutomaticChecklistManager.isMinigameCompletion(
            "You have successfully subdued the Wintertodt!"));
        assertTrue(AutomaticChecklistManager.isMinigameCompletion(
            "The Great Guardian successfully closed the rift!"));
        assertFalse(AutomaticChecklistManager.isMinigameCompletion("You entered a minigame."));
    }

    @Test
    public void onlyRecognizesExplicitBosses()
    {
        assertTrue(AutomaticChecklistManager.isBoss("Vorkath"));
        assertTrue(AutomaticChecklistManager.isBoss("General Graardor"));
        assertFalse(AutomaticChecklistManager.isBoss("Abyssal demon"));
    }
}
