package com.adventureplanner;

import static org.junit.Assert.assertEquals;

import net.runelite.api.coords.WorldPoint;
import org.junit.Test;

public class ShootingStarTrackerTest
{
    @Test
    public void mapsAllCrashedStarObjectIdsToTiers()
    {
        assertEquals(1, ShootingStarTracker.tierForObjectId(41229));
        assertEquals(2, ShootingStarTracker.tierForObjectId(41228));
        assertEquals(3, ShootingStarTracker.tierForObjectId(41227));
        assertEquals(4, ShootingStarTracker.tierForObjectId(41226));
        assertEquals(5, ShootingStarTracker.tierForObjectId(41225));
        assertEquals(6, ShootingStarTracker.tierForObjectId(41224));
        assertEquals(7, ShootingStarTracker.tierForObjectId(41223));
        assertEquals(8, ShootingStarTracker.tierForObjectId(41021));
        assertEquals(9, ShootingStarTracker.tierForObjectId(41020));
        assertEquals(0, ShootingStarTracker.tierForObjectId(1));
    }

    @Test
    public void resolvesKnownLandingLocation()
    {
        assertEquals("Catherby bank",
            StarLocationCatalog.nearestName(new WorldPoint(2804, 3434, 0)));
        assertEquals(79, StarLocationCatalog.locationCount());
    }

    @Test
    public void fallsBackToCoordinatesOutsideKnownLocations()
    {
        assertEquals("Coordinaten 100, 100",
            StarLocationCatalog.nearestName(new WorldPoint(100, 100, 0)));
    }
}
