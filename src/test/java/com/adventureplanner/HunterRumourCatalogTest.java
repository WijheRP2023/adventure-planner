package com.adventureplanner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class HunterRumourCatalogTest
{
    @Test
    public void detectsRumourAndProvidesRouteRequirements()
    {
        HunterRumour rumour = HunterRumourCatalog.detect(
            "Your rumour is to hunt red salamanders until you find the rare piece.");
        assertNotNull(rumour);
        assertEquals("Ourania Hunter area", rumour.getLocation());
        assertEquals("Small fishing net + rope per trap", rumour.getRequirements());
    }
}
