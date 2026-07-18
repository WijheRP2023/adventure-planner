package com.adventureplanner;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public final class HunterRumourCatalog
{
    private static final List<HunterRumour> RUMOURS = Collections.unmodifiableList(Arrays.asList(
        rumour("grey_chinchompa", "Grey chinchompa", "Piscatoris Hunter area", "Box traps"),
        rumour("red_chinchompa", "Red chinchompa", "Feldip Hunter area", "Box traps"),
        rumour("black_chinchompa", "Black chinchompa", "Wilderness Hunter area", "Box traps + veilig Wilderness-gear"),
        rumour("pyre_fox", "Pyre fox", "Fremennik Hunter area", "Logs + knife voor deadfall"),
        rumour("sunlight_antelope", "Sunlight antelope", "Avium Savannah", "Logs + knife voor pitfall"),
        rumour("moonlight_antelope", "Moonlight antelope", "Outer Fortis / Avium Savannah", "Logs + knife voor pitfall"),
        rumour("dashing_kebbit", "Dashing kebbit", "Piscatoris Falconry area", "500 coins; huur een gyr falcon"),
        rumour("dark_kebbit", "Dark kebbit", "Piscatoris Falconry area", "500 coins; huur een gyr falcon"),
        rumour("spotted_kebbit", "Spotted kebbit", "Piscatoris Falconry area", "500 coins; huur een gyr falcon"),
        rumour("polar_kebbit", "Polar kebbit", "Taverley Hunter area", "Noose wand"),
        rumour("sabre_kebbit", "Sabre-toothed kebbit", "Rellekka Hunter area", "Logs + knife voor deadfall"),
        rumour("razor_kebbit", "Razor-backed kebbit", "Piscatoris Hunter area", "Noose wand"),
        rumour("red_salamander", "Red salamander", "Ourania Hunter area", "Small fishing net + rope per trap"),
        rumour("orange_salamander", "Orange salamander", "Uzer Hunter area", "Small fishing net + rope per trap"),
        rumour("tecu_salamander", "Tecu salamander", "Avium Savannah", "Small fishing net + rope per trap"),
        rumour("swamp_lizard", "Swamp lizard", "Morytania Hunter area", "Small fishing net + rope per trap"),
        rumour("tropical_wagtail", "Tropical wagtail", "Feldip Hunter area", "Bird snares"),
        rumour("herbiboar", "Herbiboar", "Fossil Island Mushroom Forest", "Secateurs optioneel; stamina en graceful helpen")
    ));

    private HunterRumourCatalog() {}

    public static HunterRumour detect(String message)
    {
        String text = message.toLowerCase(Locale.ENGLISH);
        for (HunterRumour rumour : RUMOURS)
        {
            if (text.contains(rumour.getName().toLowerCase(Locale.ENGLISH))) return rumour;
        }
        return null;
    }

    public static HunterRumour find(String id)
    {
        if (id == null) return null;
        for (HunterRumour rumour : RUMOURS)
        {
            if (rumour.getId().equals(id)) return rumour;
        }
        return null;
    }

    public static List<HunterRumour> rumours()
    {
        return RUMOURS;
    }

    private static HunterRumour rumour(String id, String name, String location, String requirements)
    {
        return new HunterRumour(id, name, location, requirements);
    }
}
