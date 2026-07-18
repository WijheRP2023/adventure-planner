package com.adventureplanner;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

public final class AdventurePlannerOverlay extends Overlay
{
    private final AdventurePlannerConfig config;
    private final PlannerState state;
    private final PanelComponent panel = new PanelComponent();

    public AdventurePlannerOverlay(AdventurePlannerPlugin plugin, AdventurePlannerConfig config,
        PlannerState state)
    {
        super(plugin);
        this.config = config;
        this.state = state;
        setPosition(OverlayPosition.TOP_LEFT);
        setLayer(OverlayLayer.ABOVE_WIDGETS);
        panel.setPreferredSize(new Dimension(190, 0));
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        if (!config.showGameOverlay()) return null;
        panel.getChildren().clear();
        panel.getChildren().add(TitleComponent.builder()
            .text("Adventure Planner")
            .color(new Color(232, 160, 48))
            .build());

        GeneratedTask task = state.currentGeneratedTask();
        long now = System.currentTimeMillis();
        if (task == null)
        {
            panel.getChildren().add(LineComponent.builder().left(t("Nog geen actieve taak")).build());
        }
        else
        {
            panel.getChildren().add(LineComponent.builder()
                .left(task.getDisplayPhase(config.language())).leftColor(new Color(190, 190, 190)).build());
            addWrappedTaskTitle(task.getDisplayTitle(config.language()));
            int target = task.getTargetAmount();
            if (target > 0)
            {
                int progress = Math.min(target, state.currentTaskProgress());
                boolean complete = progress >= target;
                panel.getChildren().add(LineComponent.builder().left(t("Voortgang"))
                    .right(progress + " / " + target + (complete ? " " + t("KLAAR") : ""))
                    .rightColor(complete ? new Color(91, 173, 112) : new Color(232, 160, 48)).build());
            }
            long taskEndsAt = state.currentTaskEndsAt();
            if (taskEndsAt > 0L)
            {
                boolean expired = taskEndsAt <= now;
                panel.getChildren().add(LineComponent.builder().left(t("Taaktijd"))
                    .right(expired ? "00:00:00" : taskRemaining(taskEndsAt - now))
                    .rightColor(expired ? new Color(214, 103, 85) : new Color(91, 173, 112)).build());
            }
        }

        RunLocation next = null;
        long nextReady = Long.MAX_VALUE;
        for (RunLocation location : RunLocationCatalog.locations())
        {
            if (!visible(location.getType())) continue;
            long readyAt = state.activityReadyAt(location.timerId());
            if (readyAt > now && readyAt < nextReady)
            {
                next = location;
                nextReady = readyAt;
            }
        }
        if (next != null)
        {
            panel.getChildren().add(LineComponent.builder()
                .left(t("Volgende timer"))
                .right(next.getName() + " " + remaining(nextReady - now))
                .rightColor(new Color(91, 173, 112)).build());
        }
        return panel.render(graphics);
    }

    private String t(String text)
    {
        return PlannerText.translate(config.language(), text);
    }

    private void addWrappedTaskTitle(String title)
    {
        StringBuilder line = new StringBuilder();
        for (String word : title.split(" "))
        {
            if (line.length() > 0 && line.length() + word.length() + 1 > 25)
            {
                panel.getChildren().add(LineComponent.builder().left(line.toString()).build());
                line.setLength(0);
            }
            if (line.length() > 0) line.append(' ');
            line.append(word);
        }
        if (line.length() > 0)
        {
            panel.getChildren().add(LineComponent.builder().left(line.toString()).build());
        }
    }

    private static String taskRemaining(long millis)
    {
        long totalSeconds = Math.max(0L, (millis + 999L) / 1000L);
        return String.format("%02d:%02d:%02d", totalSeconds / 3600L,
            totalSeconds % 3600L / 60L, totalSeconds % 60L);
    }

    private boolean visible(RunType type)
    {
        switch (type)
        {
            case HERB: return config.showHerbRuns();
            case TREE: return config.showTreeRuns();
            case BIRDHOUSE: return config.showBirdhouses();
            case SEAWEED: return config.showSeaweedRuns();
            default: return false;
        }
    }

    private static String remaining(long millis)
    {
        long total = Math.max(1, (millis + 59_999L) / 60_000L);
        return total >= 60 ? total / 60 + "u " + total % 60 + "m" : total + "m";
    }
}
