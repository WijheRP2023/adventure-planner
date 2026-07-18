package com.adventureplanner;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class AdventurePlannerPluginTest
{
    public static void main(String[] args) throws Exception
    {
        ExternalPluginManager.loadBuiltin(AdventurePlannerPlugin.class);
        RuneLite.main(args);
    }
}
