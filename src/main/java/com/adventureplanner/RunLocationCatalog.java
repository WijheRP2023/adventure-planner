package com.adventureplanner;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.runelite.api.coords.WorldPoint;

public final class RunLocationCatalog
{
    private static final List<RunLocation> LOCATIONS = Collections.unmodifiableList(Arrays.asList(
        herb("herb_ardougne", "Ardougne", 2670, 3374, "1 herb seed + seed dibber",
            "Ardougne cloak of POH jewellery box naar Fishing Guild"),
        herb("herb_catherby", "Catherby", 2810, 3463, "1 herb seed + seed dibber",
            "Catherby teleport, Camelot teleport of POH portal"),
        herb("herb_falador", "Falador", 3058, 3311, "1 herb seed + seed dibber",
            "Explorer's ring 2+; anders Falador teleport"),
        herb("herb_farming_guild", "Farming Guild", 1238, 3726, "1 herb seed + seed dibber",
            "Skills necklace of POH jewellery box"),
        herb("herb_hosidius", "Hosidius", 1740, 3551, "1 herb seed + seed dibber",
            "Xeric's talisman naar Glade; ook bruikbaar vanuit de POH"),
        herb("herb_morytania", "Morytania", 3604, 3529, "1 herb seed + seed dibber",
            "Ectophial of fairy ring ALQ"),
        herb("herb_troll", "Troll Stronghold", 2828, 3696, "1 herb seed + seed dibber",
            "Stony basalt; anders Trollheim teleport"),
        herb("herb_weiss", "Weiss", 2848, 3934, "1 herb seed + seed dibber",
            "Icy basalt of Weiss-portal in de POH"),
        herb("herb_harmony", "Harmony Island", 3798, 2833, "1 herb seed + seed dibber",
            "Harmony Island teleport op Arceuus spellbook"),

        tree("tree_lumbridge", "Lumbridge tree", 3193, 3229, "1 passende sapling + spade",
            "Lumbridge teleport of POH jewellery box naar Lumbridge"),
        tree("tree_varrock", "Varrock tree", 3229, 3459, "1 passende sapling + spade",
            "Varrock teleport of POH portal"),
        tree("tree_falador", "Falador tree", 3003, 3373, "1 passende sapling + spade",
            "Falador teleport of POH portal"),
        tree("tree_taverley", "Taverley tree", 2936, 3438, "1 passende sapling + spade",
            "Taverley teleport; anders POH naar Taverley"),
        tree("tree_gnome", "Gnome Stronghold tree", 2436, 3418, "1 passende sapling + spade",
            "Spirit tree vanuit Grand Exchange of POH"),
        tree("tree_guild", "Farming Guild tree", 1233, 3734, "1 passende sapling + spade",
            "Skills necklace of POH jewellery box"),
        tree("fruit_catherby", "Catherby fruit tree", 2860, 3434, "1 fruit-tree sapling + spade",
            "Catherby teleport of Camelot teleport"),
        tree("fruit_brimhaven", "Brimhaven fruit tree", 2764, 3213, "1 fruit-tree sapling + spade",
            "Spirit tree naar Brimhaven of POH portal"),
        tree("fruit_gnome", "Gnome Stronghold fruit tree", 2474, 3446, "1 fruit-tree sapling + spade",
            "Spirit tree vanuit Grand Exchange of POH"),
        tree("fruit_village", "Tree Gnome Village fruit tree", 2490, 3180, "1 fruit-tree sapling + spade",
            "Spirit tree naar Tree Gnome Village"),
        tree("fruit_lletya", "Lletya fruit tree", 2346, 3161, "1 fruit-tree sapling + spade",
            "Teleport crystal naar Lletya"),
        tree("fruit_guild", "Farming Guild fruit tree", 1244, 3757, "1 fruit-tree sapling + spade",
            "Skills necklace of POH jewellery box"),

        tree("hardwood_fossil_east", "Fossil Island (East)", 3703, 3836,
            "1 hardwood sapling + spade", "Digsite pendant; mushroom transport naar Mushroom Meadow"),
        tree("hardwood_fossil_middle", "Fossil Island (Middle)", 3701, 3834,
            "1 hardwood sapling + spade", "Digsite pendant; mushroom transport naar Mushroom Meadow"),
        tree("hardwood_fossil_west", "Fossil Island (West)", 3699, 3836,
            "1 hardwood sapling + spade", "Digsite pendant; mushroom transport naar Mushroom Meadow"),

        tree("spirit_etceteria", "Etceteria", 2590, 3858,
            "1 spirit sapling + spade", "Fairy ring CIP en loop naar Etceteria"),
        tree("spirit_port_sarim", "Port Sarim", 3058, 3258,
            "1 spirit sapling + spade", "Explorer's ring of POH jewellery box"),
        tree("spirit_brimhaven", "Brimhaven", 2802, 3207,
            "1 spirit sapling + spade", "POH portal, charter of Karamja gloves"),
        tree("spirit_hosidius", "Hosidius", 1693, 3540,
            "1 spirit sapling + spade", "Xeric's talisman naar Glade"),
        tree("spirit_farming_guild", "Farming Guild", 1248, 3753,
            "1 spirit sapling + spade", "Skills necklace of POH jewellery box"),

        tree("special_calquat", "Tai Bwo Wannai calquat", 2796, 3101,
            "1 calquat sapling + spade", "Tai bwo wannai teleport of fairy ring CKR"),
        tree("special_crystal", "Lletya crystal tree", 2340, 3162,
            "1 crystal acorn + spade", "Teleport crystal naar Lletya"),
        tree("special_celastrus", "Farming Guild celastrus", 1234, 3758,
            "1 celastrus sapling + spade", "Skills necklace of POH jewellery box"),
        tree("special_redwood", "Farming Guild redwood", 1231, 3744,
            "1 redwood sapling + spade", "Skills necklace of POH jewellery box"),

        bird("bird_meadow", "Mushroom Meadow", 3678, 3871,
            "1 clockwork, 1 log en 10 hop seeds", "Digsite pendant naar Fossil Island; daarna mushroom transport"),
        bird("bird_valley_north", "Verdant Valley noord", 3764, 3758,
            "1 clockwork, 1 log en 10 hop seeds", "Digsite pendant naar Fossil Island; daarna mushroom transport"),
        bird("bird_valley_south", "Verdant Valley zuid", 3767, 3752,
            "1 clockwork, 1 log en 10 hop seeds", "Digsite pendant naar Fossil Island; daarna mushroom transport"),
        bird("bird_house_hill", "Mushroom Forest", 3679, 3814,
            "1 clockwork, 1 log en 10 hop seeds", "Digsite pendant naar Fossil Island; daarna mushroom transport"),

        sea("seaweed_north", "Giant seaweed noord", 3749, 10278,
            "1 seaweed spore + seed dibber", "Digsite pendant naar Fossil Island en duik bij de rowboat"),
        sea("seaweed_south", "Giant seaweed zuid", 3753, 10276,
            "1 seaweed spore + seed dibber", "Digsite pendant naar Fossil Island en duik bij de rowboat")
    ));

    private RunLocationCatalog() {}

    public static List<RunLocation> locations()
    {
        return LOCATIONS;
    }

    public static RunLocation nearest(RunType type, WorldPoint player)
    {
        if (player == null) return null;
        RunLocation best = null;
        int bestDistance = Integer.MAX_VALUE;
        for (RunLocation location : LOCATIONS)
        {
            if (location.getType() != type || location.getPoint().getPlane() != player.getPlane()) continue;
            int dx = location.getPoint().getX() - player.getX();
            int dy = location.getPoint().getY() - player.getY();
            int distance = dx * dx + dy * dy;
            if (distance < bestDistance)
            {
                best = location;
                bestDistance = distance;
            }
        }
        return bestDistance <= 45 * 45 ? best : null;
    }

    private static RunLocation herb(String id, String name, int x, int y, String items, String teleport)
    { return location(id, RunType.HERB, name, x, y, items, teleport); }
    private static RunLocation tree(String id, String name, int x, int y, String items, String teleport)
    { return location(id, RunType.TREE, name, x, y, items, teleport); }
    private static RunLocation bird(String id, String name, int x, int y, String items, String teleport)
    { return location(id, RunType.BIRDHOUSE, name, x, y, items, teleport); }
    private static RunLocation sea(String id, String name, int x, int y, String items, String teleport)
    { return location(id, RunType.SEAWEED, name, x, y, items, teleport); }
    private static RunLocation location(String id, RunType type, String name, int x, int y,
        String items, String teleport)
    { return new RunLocation(id, type, name, new WorldPoint(x, y, 0), items, teleport); }
}
