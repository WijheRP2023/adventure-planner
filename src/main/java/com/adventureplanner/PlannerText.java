package com.adventureplanner;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class PlannerText
{
    private static final Map<String, String> ENGLISH = new HashMap<>();
    private static final Pattern QUEST_WORK =
        Pattern.compile("^Werk (\\d+) minuten aan (.+)$");

    static
    {
        put("TAKEN", "TASKS");
        put("TAAK", "TASK");
        put("ACCOUNT ROUTE", "ACCOUNT JOURNEY");
        put("Afwisseling met richting", "Variety with purpose");
        put("Dagelijkse routine", "Daily routine");
        put("Eenmalige taken die je vandaag kunt afronden",
            "One-off tasks you can complete today");
        put("Weekoverzicht", "Weekly overview");
        put("Kleine doelen voor extra afwisseling", "Small goals for extra variety");
        put("Terugkerende runs", "Recurring runs");
        put("Timers per locatie; deze tellen niet mee met je dagelijkse routine",
            "Timers per location; these do not count towards your daily routine");
        put("Geen timers geselecteerd", "No timers selected");
        put("Zet een run aan onder Instellingen > Timers en runs",
            "Enable a run under Settings > Timers and runs");
        put("Shooting stars", "Shooting stars");
        put("Automatisch gevonden sterren met locatie, wereld en tier",
            "Automatically detected stars with location, world and tier");
        put("Nog geen shooting star gezien", "No shooting star detected yet");
        put("De scanner vult deze lijst automatisch zodra een crashed star in de geladen spelwereld verschijnt.",
            "The scanner fills this list automatically when a crashed star appears in the loaded game world.");
        put("Nu zichtbaar in de geladen omgeving", "Currently visible in the loaded area");
        put("VOLGENDE STAP", "NEXT STEP");
        put("Nog geen taak gekozen", "No task selected");
        put("Log in en laat je volgende doel kiezen.", "Log in to select your next goal.");
        put("Je levels, bank en recente taken bepalen de keuze.",
            "Your levels, bank and recent tasks determine the choice.");
        put("GEEN ACTIEVE TIMER", "NO ACTIVE TIMER");
        put("SKIP / WISSEL TAAK", "SKIP / CHANGE TASK");
        put("AFRONDEN & VOLGENDE", "COMPLETE & NEXT");
        put("STOP TAAK (HANDMATIG)", "STOP TASK (MANUALLY)");
        put("GEGEVENSBRONNEN", "DATA SOURCES");
        put("PERSOONLIJKE NOTITIES", "PERSONAL NOTES");
        put("Nog niet gescand", "Not scanned yet");
        put("Snapshot beschikbaar", "Snapshot available");
        put("Open je bank om voorraad te scannen", "Open your bank to scan supplies");
        put("Open je Seed Vault om seeds te scannen",
            "Open your Seed Vault to scan seeds");
        put("WACHTEN OP LOGIN", "WAITING FOR LOGIN");
        put("Log eerst in", "Log in first");
        put("Daarna kan ik je echte levels en inventory lezen.",
            "Then I can read your actual levels and inventory.");
        put("ALLES KLAAR", "ALL DONE");
        put("Alle beschikbare skills zijn 99 of geblokkeerd.",
            "All available skills are level 99 or disabled.");
        put("HANDMATIG GESTOPT", "STOPPED MANUALLY");
        put("Automatische taken gepauzeerd", "Automatic tasks paused");
        put("Druk op skip / wissel taak om weer verder te gaan.",
            "Press skip / change task to continue.");
        put("TIJD VOORBIJ", "TIME EXPIRED");
        put("Volgende taak wordt gekozen", "Selecting the next task");
        put("TAAKTIJD", "TASK TIME");
        put("VOORTGANG", "PROGRESS");
        put("KLAAR", "DONE");
        put("Voorraad/activiteit", "Supply/activity");
        put("Korte taak", "Short task");
        put("Diary-richting", "Diary path");
        put("Focus", "Focus");
        put("Nog niet gestart", "Not started");
        put("Klaar", "Ready");
        put("Done", "Done");
        put("Normale bomen", "Regular trees");
        put("Speciale bomen", "Special trees");
        put("Herb patches", "Herb patches");
        put("Giant seaweed", "Giant seaweed");
        put("Birdhouses", "Birdhouses");
        put("Fruit trees", "Fruit trees");
        put("Hardwood trees", "Hardwood trees");
        put("Spirit trees", "Spirit trees");
        put("Zaff: battlestaves ophalen", "Zaff: collect battlestaves");
        put("Bert: emmers zand ophalen", "Bert: collect buckets of sand");
        put("Thirus: gratis dynamiet ophalen", "Thirus: collect free dynamite");
        put("Wizard Cromperty: rune essence ophalen",
            "Wizard Cromperty: collect rune essence");
        put("Rantz: ogre arrows ophalen", "Rantz: collect ogre arrows");
        put("Robin: bones omzetten in bonemeal", "Robin: turn bones into bonemeal");
        put("Herb-run doen", "Complete a herb run");
        put("Tree- of fruit-tree-run doen", "Complete a tree or fruit-tree run");
        put("Giant seaweed-run doen", "Complete a giant seaweed run");
        put("Volledige Birdhouse-run doen", "Complete a full birdhouse run");
        put("Farming Contract controleren", "Check Farming Contract");
        put("Hunter Rumour doen", "Complete a Hunter Rumour");
        put("Kingdom-goedkeuring en resources", "Kingdom approval and resources");
        put("Een afwisselende minigame spelen", "Play a varied minigame");
        put("Een nieuwe of vergeten boss proberen", "Try a new or forgotten boss");
        put("Dagelijks", "Daily");
        put("Wekelijks", "Weekly");
        put("Nog niet afgerond", "Not completed");
        put("Voortgang", "Progress");
        put("Taaktijd", "Task time");
        put("Voorraadketen: verzamel exact deze logs; daarna kan de planner ze verwerken",
            "Supply chain: collect these exact logs; the planner can process them next");
        put("Voorraadketen: vang exact deze vis; daarna kan de planner hem laten koken",
            "Supply chain: catch this exact fish; the planner can cook it next");
        put("Voorraadketen: verzamel exact deze ore; daarna kan de planner bars laten maken",
            "Supply chain: collect this exact ore; the planner can make bars next");
        put("Volgende timer", "Next timer");
    }

    private PlannerText()
    {
    }

    static String translate(PlannerLanguage language, String text)
    {
        if (text == null || language == null || !language.isEnglish()) return text;
        String exact = ENGLISH.get(text);
        if (exact != null) return exact;

        Matcher quest = QUEST_WORK.matcher(text);
        if (quest.matches())
        {
            return "Work on " + quest.group(2) + " for " + quest.group(1) + " minutes";
        }

        String value = text;
        value = start(value, "Reanimeer en versla ", "Reanimate and defeat ");
        value = start(value, "Maak schoon ", "Clean ");
        value = start(value, "Voltooi ", "Complete ");
        value = start(value, "Verbrand ", "Burn ");
        value = start(value, "Verwerk ", "Process ");
        value = start(value, "Versla ", "Defeat ");
        value = start(value, "Begraaf ", "Bury ");
        value = start(value, "Verdien ", "Earn ");
        value = start(value, "Gebruik ", "Use ");
        value = start(value, "Neem mee: ", "Bring: ");
        value = start(value, "Neem: ", "Bring: ");
        value = start(value, "Neem ", "Bring ");
        value = start(value, "Slijp ", "Cut ");
        value = start(value, "Kook ", "Cook ");
        value = start(value, "Maak ", "Make ");
        value = start(value, "Blaas ", "Blow ");
        value = start(value, "Hak ", "Chop ");
        value = start(value, "Vang ", "Catch ");
        value = start(value, "Vis ", "Fish ");
        value = start(value, "Speel ", "Play ");
        value = start(value, "Doe ", "Do ");
        value = start(value, "Werk aan ", "Work on ");
        value = start(value, "Steel ", "Steal ");

        value = value.replace("stop na maximaal", "stop after at most")
            .replace("Stop na maximaal", "Stop after at most")
            .replace("stop na de ingestelde taakduur", "stop when the task timer ends")
            .replace("Stop na de timer", "Stop when the timer ends")
            .replace("Voorraadketen:", "Supply chain:")
            .replace("verzamel exact deze", "collect these exact")
            .replace("daarna kan de planner", "then the planner can")
            .replace("gebruikt bestaande voorraad", "uses existing supplies")
            .replace("in je bank", "in your bank")
            .replace("uit de bank", "from the bank")
            .replace("bankvoorraad", "bank supplies")
            .replace("voorbereiding voor", "preparation for")
            .replace("-richting:", " path:")
            .replace("vereist level", "requires level")
            .replace("Vereist toegang tot", "Requires access to")
            .replace("vereist toegang tot", "requires access to")
            .replace("wanneer beschikbaar", "when available")
            .replace("zodra je dat hebt", "once you have it")
            .replace("als die verschijnen", "when they appear")
            .replace("Neem", "Bring")
            .replace("Gebruik", "Use")
            .replace("Verzamel", "Collect")
            .replace("geen vaste methode", "no fixed method")
            .replace("Afwisselende", "Varied")
            .replace("afwisselende", "varied")
            .replace("korte", "short")
            .replace("rustige", "relaxed")
            .replace(" minuten", " minutes")
            .replace(" uur", " hours")
            .replace(" voor ", " for ")
            .replace(" met ", " with ")
            .replace(" en ", " and ")
            .replace(" of ", " or ")
            .replace(" naar ", " to ")
            .replace(" bij ", " at ")
            .replace(" per plek", " per location")
            .replace("soorten items", "item types")
            .replace("soorten seeds", "seed types")
            .replace("Laatst gezien", "Last seen")
            .replace(" geleden", " ago")
            .replace("Wereld ", "World ")
            .replace("WERELD ", "WORLD ")
            .replace("Locatie:", "Location:")
            .replace("Coordinaten", "Coordinates")
            .replace("Nog niet", "Not yet")
            .replace("eerst", "first")
            .replace("gratis", "free");
        return value;
    }

    private static String start(String value, String dutch, String english)
    {
        return value.startsWith(dutch) ? english + value.substring(dutch.length()) : value;
    }

    private static void put(String dutch, String english)
    {
        ENGLISH.put(dutch, english);
    }
}
