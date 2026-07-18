package com.adventureplanner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import net.runelite.api.Skill;

public final class SkillActivityCatalog
{
    private static final List<SkillActivity> ACTIVITIES = Collections.unmodifiableList(Arrays.asList(
        activity(Skill.MAGIC, 7, "Doe {minutes} minuten Mage Training Arena", "Wissel spreuken af en werk aan nuttige beloningen"),

        activity(Skill.FISHING, 15, "Speel 1 Fishing Trawler-run", "Afwisselende Fishing-activiteit met outfitkans"),
        activity(Skill.FISHING, 35, "Speel Tempoross voor {minutes} minuten", "Fishing-boss; stop na de ingestelde taakduur"),
        activity(Skill.FIREMAKING, 50, "Speel Wintertodt voor {minutes} minuten", "Firemaking-boss met korte afzonderlijke games"),
        activity(Skill.SMITHING, 15, "Speel Giants' Foundry voor {minutes} minuten", "Gebruik bars of ongewenste metalen uitrusting"),
        activity(Skill.SMITHING, 15, "Gebruik de Blast Furnace {minutes} minuten", "Verwerk ore efficiënt; controleer coal en bars in de bank"),
        activity(Skill.MINING, 10, "Mine een Shooting Star voor {minutes} minuten", "Rustige Mining XP en stardust; iedere sterlaag vanaf tier 1 is bruikbaar"),
        activity(Skill.MINING, 30, "Doe Motherlode Mine voor {minutes} minuten", "Afk Mining en nuttige ore-voorraad"),
        activity(Skill.MINING, 50, "Doe Volcanic Mine voor {minutes} minuten", "Teamactiviteit; controleer eerst de toegangsvereisten"),
        activity(Skill.HERBLORE, 60, "Speel Mastering Mixology voor {minutes} minuten", "Herblore-activiteit met eigen beloningen"),
        activity(Skill.AGILITY, 40, "Doe Brimhaven Agility Arena {minutes} minuten", "Verzamel tickets voor een andere trainingsstijl"),
        activity(Skill.AGILITY, 52, "Doe Hallowed Sepulchre voor {minutes} minuten", "Vereist toegang tot Darkmeyer"),
        activity(Skill.THIEVING, 21, "Doe Pyramid Plunder voor {minutes} minuten", "Korte rondes en kans op de sceptre"),
        activity(Skill.THIEVING, 49, "Steel artefacts voor {minutes} minuten", "Afwisselende en actieve Thieving-methode"),
        activity(Skill.SLAYER, 1, "Werk {minutes} minuten aan je Slayer task", "Stop na de timer, ook als de assignment nog niet af is"),
        activity(Skill.FARMING, 34, "Doe Tithe Farm voor {minutes} minuten", "Gebruik de hoogste Tithe-seed die je level toestaat"),
        activity(Skill.RUNECRAFT, 27, "Speel Guardians of the Rift voor {minutes} minuten", "Runecraft-minigame; vereist Temple of the Eye"),
        activity(Skill.HUNTER, 46, "Voltooi 1 Hunter Rumour", "Laat de guild een afwisselende Hunter-methode kiezen"),
        activity(Skill.HUNTER, 80, "Doe Herbiboar voor {minutes} minuten", "Hunter-activiteit met herbs als extra voorraad"),
        activity(Skill.CONSTRUCTION, 1, "Doe Mahogany Homes voor {minutes} minuten", "Construction-contracten met minder plankverbruik"),
        activity(Skill.SAILING, 1, "Voltooi Sailing Port Tasks voor {minutes} minuten", "Courier- of bounty-taken vanaf notice boards"),
        activity(Skill.SAILING, 1, "Werk aan Sea Charting voor {minutes} minuten", "Eenmalige ontdekkingen met gebiedsbonussen"),
        activity(Skill.SAILING, 15, "Doe Shipwreck Salvaging voor {minutes} minuten", "Afwisselende Sailing-training vanaf level 15")
    ));

    private SkillActivityCatalog() {}

    public static List<BankTaskCandidate> candidates(Map<Skill, Integer> levels,
        List<Skill> excluded, int minutes)
    {
        List<BankTaskCandidate> result = new ArrayList<>();
        for (SkillActivity activity : ACTIVITIES)
        {
            int level = levels.getOrDefault(activity.getSkill(), 1);
            if (level >= activity.getMinimumLevel() && level < 99 && !excluded.contains(activity.getSkill()))
            {
                result.add(new BankTaskCandidate(activity.getSkill(), activity.title(minutes),
                    activity.getDetail() + " · vereist level " + activity.getMinimumLevel()));
            }
        }
        return result;
    }

    public static List<SkillActivity> activities()
    {
        return ACTIVITIES;
    }

    private static SkillActivity activity(Skill skill, int level, String title, String detail)
    {
        return new SkillActivity(skill, level, title, detail);
    }
}
