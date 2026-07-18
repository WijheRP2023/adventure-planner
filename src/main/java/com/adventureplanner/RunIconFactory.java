package com.adventureplanner;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.swing.Icon;
import javax.swing.ImageIcon;

public final class RunIconFactory
{
    private RunIconFactory() {}

    public static Icon icon(RunType type)
    {
        BufferedImage image = new BufferedImage(14, 14, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        switch (type)
        {
            case HERB:
                graphics.setColor(new Color(91, 173, 112));
                graphics.fillOval(2, 1, 8, 11);
                graphics.setColor(new Color(184, 221, 139));
                graphics.drawLine(4, 11, 11, 3);
                break;
            case TREE:
                graphics.setColor(new Color(130, 88, 48));
                graphics.fillRect(6, 7, 3, 7);
                graphics.setColor(new Color(76, 148, 72));
                graphics.fillOval(1, 0, 12, 10);
                break;
            case BIRDHOUSE:
                graphics.setColor(new Color(221, 151, 55));
                int[] x = {1, 7, 13};
                int[] y = {6, 1, 6};
                graphics.fillPolygon(x, y, 3);
                graphics.fillRect(3, 6, 8, 7);
                graphics.setColor(new Color(35, 35, 35));
                graphics.fillOval(6, 7, 3, 3);
                break;
            case SEAWEED:
                graphics.setColor(new Color(74, 158, 192));
                graphics.drawArc(0, 3, 8, 7, 200, 150);
                graphics.drawArc(5, 5, 8, 6, 200, 150);
                graphics.setColor(new Color(91, 173, 112));
                graphics.drawLine(7, 12, 5, 3);
                graphics.drawLine(7, 12, 10, 4);
                break;
            default:
                break;
        }
        graphics.dispose();
        return new ImageIcon(image);
    }
    public static Icon locationIcon(RunType type)
    {
        ImageIcon small = (ImageIcon) icon(type);
        return new ImageIcon(small.getImage().getScaledInstance(22, 22,
            java.awt.Image.SCALE_SMOOTH));
    }

    public static Icon soundIcon(boolean enabled)
    {
        BufferedImage image = new BufferedImage(18, 18, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND,
            BasicStroke.JOIN_ROUND));
        graphics.setColor(enabled ? new Color(226, 157, 58) : new Color(120, 120, 120));
        graphics.fillRoundRect(6, 4, 6, 9, 5, 5);
        graphics.drawLine(4, 7, 4, 10);
        graphics.drawLine(4, 7, 6, 5);
        graphics.drawLine(4, 10, 6, 12);
        if (enabled)
        {
            graphics.drawArc(9, 3, 7, 12, -65, 130);
        }
        else
        {
            graphics.setColor(new Color(165, 165, 165));
            graphics.drawLine(3, 3, 15, 15);
        }
        graphics.dispose();
        return new ImageIcon(image);
    }
}
