package com.adventureplanner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.EnumSet;
import net.runelite.api.coords.WorldPoint;
import org.junit.Test;

public class RunLocationCatalogTest
{
    @Test
    public void everyRunTypeHasLocationsAndUniqueTimerIds()
    {
        EnumSet<RunType> types = EnumSet.noneOf(RunType.class);
        java.util.Set<String> ids = new java.util.HashSet<>();
        for (RunLocation location : RunLocationCatalog.locations())
        {
            types.add(location.getType());
            assertTrue(ids.add(location.timerId()));
        }
        assertEquals(EnumSet.allOf(RunType.class), types);
    }

    @Test
    public void nearestHerbPatchUsesPlayerLocation()
    {
        RunLocation location = RunLocationCatalog.nearest(RunType.HERB,
            new WorldPoint(2809, 3462, 0));
        assertNotNull(location);
        assertEquals("Catherby", location.getName());
    }
}
