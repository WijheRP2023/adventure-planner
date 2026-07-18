package com.adventureplanner;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import net.runelite.api.Client;
import net.runelite.client.ui.overlay.worldmap.WorldMapPoint;
import net.runelite.client.ui.overlay.worldmap.WorldMapPointManager;

public final class RunNavigator
{
    private final Client client;
    private final WorldMapPointManager worldMapPointManager;
    private WorldMapPoint activePoint;

    public RunNavigator(Client client, WorldMapPointManager worldMapPointManager)
    {
        this.client = client;
        this.worldMapPointManager = worldMapPointManager;
    }

    public void navigate(RunLocation location)
    {
        clear();
        client.setHintArrow(location.getPoint());
        activePoint = new WorldMapPoint(location.getPoint(), marker());
        activePoint.setName(location.getName());
        activePoint.setTooltip(location.getName() + " - " + location.getTeleport());
        activePoint.setJumpOnClick(true);
        worldMapPointManager.add(activePoint);
    }

    public void clear()
    {
        if (activePoint != null)
        {
            client.clearHintArrow();
            worldMapPointManager.remove(activePoint);
        }
        activePoint = null;
    }

    private static BufferedImage marker()
    {
        BufferedImage image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setColor(new Color(35, 28, 20, 220));
        graphics.fillOval(0, 0, 15, 15);
        graphics.setColor(new Color(232, 160, 48));
        graphics.fillOval(3, 3, 9, 9);
        graphics.dispose();
        return image;
    }
}
