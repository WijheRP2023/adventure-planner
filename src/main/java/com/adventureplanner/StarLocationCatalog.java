package com.adventureplanner;

import net.runelite.api.coords.WorldPoint;

public final class StarLocationCatalog
{
    private static final Entry[] LOCATIONS =
    {
        e(2974, 3241, "Rimmington mine"),
        e(2940, 3280, "Crafting Guild"),
        e(2906, 3355, "West Falador mine"),
        e(3030, 3348, "East Falador bank"),
        e(3018, 3443, "North Dwarven Mine entrance"),
        e(2882, 3474, "Taverley house portal"),
        e(2736, 3221, "Brimhaven northwest gold mine"),
        e(2742, 3143, "Brimhaven south dungeon entrance"),
        e(2845, 3037, "Nature Altar mine north of Shilo"),
        e(2827, 2999, "Shilo Village gem mine"),
        e(2835, 3296, "North Crandor"),
        e(2822, 3238, "South Crandor"),
        e(3296, 3298, "Al Kharid mine"),
        e(3276, 3164, "Al Kharid bank"),
        e(3351, 3281, "Mage Training Arena entrance"),
        e(3424, 3160, "Northwest of Uzer"),
        e(3434, 2889, "Nardah bank"),
        e(3316, 2867, "Agility Pyramid mine"),
        e(3171, 2910, "Desert Quarry mine"),
        e(2567, 2858, "Corsair Cove bank"),
        e(2483, 2886, "Corsair Resource Area"),
        e(2468, 2842, "Myths' Guild"),
        e(2571, 2964, "Feldip Hills fairy ring"),
        e(2630, 2993, "Rantz cave"),
        e(2200, 2792, "Soul Wars south mine"),
        e(3818, 3801, "Fossil Island Volcanic Mine entrance"),
        e(3774, 3814, "Fossil Island rune rocks"),
        e(3686, 2969, "Mos Le'Harmless west bank"),
        e(2727, 3683, "Keldagrim entrance mine"),
        e(2683, 3699, "Rellekka mine"),
        e(2393, 3814, "Jatizso mine entrance"),
        e(2375, 3832, "Neitiznot south rune rock"),
        e(2528, 3887, "Miscellania mine"),
        e(2139, 3938, "Lunar Isle mine entrance"),
        e(2602, 3086, "Yanille bank"),
        e(2624, 3141, "Port Khazard mine"),
        e(2608, 3233, "Ardougne Monastery"),
        e(2705, 3333, "South of Legends' Guild"),
        e(2804, 3434, "Catherby bank"),
        e(2589, 3478, "Coal Trucks west of Seers'"),
        e(1778, 3493, "Hosidius mine"),
        e(1769, 3709, "Port Piscarilius mine"),
        e(1597, 3648, "Shayzien mine"),
        e(1534, 3747, "South Lovakengj bank"),
        e(1437, 3840, "Lovakite mine"),
        e(1760, 3853, "Arceuus dense essence mine"),
        e(1322, 3816, "Mount Karuulm bank"),
        e(1279, 3817, "Mount Karuulm mine"),
        e(1210, 3651, "Kebos Swamp mine"),
        e(1258, 3564, "Chambers of Xeric bank"),
        e(3258, 3408, "Varrock east bank"),
        e(3290, 3353, "South-east Varrock mine"),
        e(3175, 3362, "Champions' Guild mine"),
        e(3094, 3235, "Draynor Village"),
        e(3153, 3150, "West Lumbridge Swamp mine"),
        e(3230, 3155, "East Lumbridge Swamp mine"),
        e(3635, 3340, "Darkmeyer essence mine entrance"),
        e(3650, 3214, "Theatre of Blood bank"),
        e(3505, 3485, "Canifis bank"),
        e(3500, 3219, "Burgh de Rott bank"),
        e(3451, 3233, "Abandoned Mine west of Burgh"),
        e(2444, 3490, "West of Grand Tree"),
        e(2448, 3436, "Gnome Stronghold spirit tree"),
        e(2341, 3635, "Piscatoris fairy ring"),
        e(2329, 3163, "Lletya"),
        e(2269, 3158, "Isafdar runite rocks"),
        e(3274, 6055, "Prifddinas Zalcano entrance"),
        e(2318, 3269, "Arandar mine"),
        e(2173, 3409, "Mynydd northwest of Prifddinas"),
        e(3108, 3569, "Mage of Zamorak mine (level 7 Wilderness)"),
        e(3018, 3593, "Skeleton mine (level 10 Wilderness)"),
        e(3093, 3756, "Hobgoblin mine (level 30 Wilderness)"),
        e(3057, 3887, "Lava Maze runite mine (level 46 Wilderness)"),
        e(3049, 3940, "Pirates' Hideout (level 53 Wilderness)"),
        e(3091, 3962, "Mage Arena bank (level 56 Wilderness)"),
        e(3188, 3932, "Wilderness Resource Area"),
        e(1742, 2954, "Varlamore south-east mine"),
        e(1771, 3102, "Varlamore Colosseum bank"),
        e(1486, 3089, "Mine northwest of Hunter Guild")
    };

    private StarLocationCatalog() {}

    public static String nearestName(WorldPoint point)
    {
        Entry nearest = null;
        int bestDistance = Integer.MAX_VALUE;
        for (Entry entry : LOCATIONS)
        {
            int dx = entry.x - point.getX();
            int dy = entry.y - point.getY();
            int distance = dx * dx + dy * dy;
            if (distance < bestDistance)
            {
                nearest = entry;
                bestDistance = distance;
            }
        }
        return nearest != null && bestDistance <= 30 * 30
            ? nearest.name
            : "Coordinaten " + point.getX() + ", " + point.getY();
    }

    static int locationCount()
    {
        return LOCATIONS.length;
    }

    private static Entry e(int x, int y, String name)
    {
        return new Entry(x, y, name);
    }

    private static final class Entry
    {
        private final int x;
        private final int y;
        private final String name;

        private Entry(int x, int y, String name)
        {
            this.x = x;
            this.y = y;
            this.name = name;
        }
    }
}
