package com.adventureplanner;

import java.awt.Color;
import java.awt.image.BufferedImage;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.ui.overlay.infobox.InfoBox;
import net.runelite.client.ui.overlay.infobox.InfoBoxPriority;

final class TaskReminderInfoBox extends InfoBox
{
    private static final Color DAILY = new Color(232, 160, 48);
    private static final Color WEEKLY = new Color(105, 184, 220);

    private final PlannerTask task;
    private final AdventurePlannerConfig config;

    TaskReminderInfoBox(BufferedImage image, Plugin plugin, PlannerTask task)
    {
        this(image, plugin, task, null);
    }

    TaskReminderInfoBox(BufferedImage image, Plugin plugin, PlannerTask task,
        AdventurePlannerConfig config)
    {
        super(image, plugin);
        this.task = task;
        this.config = config;
        setPriority(InfoBoxPriority.MED);
    }

    String taskId()
    {
        return task.getId();
    }

    @Override
    public String getText()
    {
        return task.getCadence() == TaskCadence.DAILY ? "D" : "W";
    }

    @Override
    public Color getTextColor()
    {
        return task.getCadence() == TaskCadence.DAILY ? DAILY : WEEKLY;
    }

    @Override
    public String getTooltip()
    {
        PlannerLanguage language = config == null ? PlannerLanguage.DUTCH : config.language();
        String cadence = task.getCadence() == TaskCadence.DAILY ? "Dagelijks" : "Wekelijks";
        return PlannerText.translate(language, cadence) + " - "
            + (task.isCustom() ? task.getTitle() : PlannerText.translate(language, task.getTitle()))
            + " - " + PlannerText.translate(language, "Nog niet afgerond");
    }

    @Override
    public String getName()
    {
        PlannerLanguage language = config == null ? PlannerLanguage.DUTCH : config.language();
        return "Adventure Planner: " + (task.isCustom() ? task.getTitle()
            : PlannerText.translate(language, task.getTitle()));
    }

}
