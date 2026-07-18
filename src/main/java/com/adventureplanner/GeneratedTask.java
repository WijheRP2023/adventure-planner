package com.adventureplanner;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.runelite.api.Skill;

public final class GeneratedTask
{
    private static final Pattern TARGET_AMOUNT = Pattern.compile("\\b(\\d+)\\b");
    private final Skill skill;
    private final int startLevel;
    private final int targetLevel;
    private final String phase;
    private final String reason;
    private final String explicitTitle;

    public GeneratedTask(Skill skill, int startLevel, int targetLevel, String phase, String reason)
    {
        this(skill, startLevel, targetLevel, phase, reason, null);
    }

    private GeneratedTask(Skill skill, int startLevel, int targetLevel, String phase, String reason, String explicitTitle)
    {
        this.skill = skill;
        this.startLevel = startLevel;
        this.targetLevel = targetLevel;
        this.phase = phase;
        this.reason = reason;
        this.explicitTitle = explicitTitle;
    }

    public Skill getSkill() { return skill; }
    public int getStartLevel() { return startLevel; }
    public int getTargetLevel() { return targetLevel; }
    public String getPhase() { return phase; }
    public String getReason() { return reason; }

    public String getTitle()
    {
        return explicitTitle == null
            ? skill.getName() + ": level " + startLevel + " → " + targetLevel
            : explicitTitle;
    }

    public String getDisplayPhase(PlannerLanguage language)
    {
        return PlannerText.translate(language, getPhase());
    }

    public String getDisplayTitle(PlannerLanguage language)
    {
        return PlannerText.translate(language, getTitle());
    }

    public String getDisplayReason(PlannerLanguage language)
    {
        return PlannerText.translate(language, getReason());
    }

    public int getTargetAmount()
    {
        if (explicitTitle == null || explicitTitle.toLowerCase().contains("minuten")) return 0;
        Matcher matcher = TARGET_AMOUNT.matcher(explicitTitle);
        if (!matcher.find()) return 0;
        try
        {
            return Integer.parseInt(matcher.group(1));
        }
        catch (NumberFormatException ignored) { return 0; }
    }

    public int startingInventoryProgress(Map<String, Integer> items)
    {
        if (explicitTitle == null || items == null) return 0;
        String title = explicitTitle.toLowerCase();
        int amount = 0;
        for (Map.Entry<String, Integer> entry : items.entrySet())
        {
            String item = entry.getKey().toLowerCase();
            boolean matchesLogs = title.contains("logs")
                && (item.equals("logs") || item.endsWith(" logs"));
            boolean matchesOre = title.contains(" ore")
                && item.endsWith(" ore");
            if (matchesLogs || matchesOre)
            {
                amount += Math.max(0, entry.getValue());
            }
        }
        return Math.min(getTargetAmount(), amount);
    }


    public static GeneratedTask activity(Skill skill, String title, String detail)
    {
        return new GeneratedTask(skill, 0, 0, "Voorraad/activiteit", detail, title);
    }

    public static GeneratedTask focus(String title, String detail)
    {
        return new GeneratedTask(Skill.OVERALL, 0, 0, "Focus", detail, title);
    }

    public static GeneratedTask shortProgression(GeneratedTask goal, int minutes)
    {
        String detail = goal.getPhase() + "-richting: level " + goal.getTargetLevel()
            + " · " + goal.getReason();
        return new GeneratedTask(goal.getSkill(), goal.getStartLevel(), goal.getTargetLevel(),
            "Korte taak", detail, "Train " + goal.getSkill().getName() + " " + minutes + " minuten");
    }
}
