package com.adventureplanner;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.ObjectComposition;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameObjectDespawned;
import net.runelite.api.events.GameObjectSpawned;

public final class ShootingStarTracker
{
    private static final long KEEP_SIGHTING_MS = 2L * 60L * 60L * 1000L;
    private static final int[] TIER_IDS =
        {41229, 41228, 41227, 41226, 41225, 41224, 41223, 41021, 41020};

    private final Client client;
    private final Map<Integer, ShootingStarSighting> sightings = new LinkedHashMap<>();
    private long lastSceneScanAt;
    private int lastSceneScanWorld;

    public ShootingStarTracker(Client client)
    {
        this.client = client;
    }

    public boolean onGameObjectSpawned(GameObjectSpawned event)
    {
        return observe(event.getGameObject());
    }
    public boolean scanLoadedScene()
    {
        if (client.getScene() == null) return false;
        long now = System.currentTimeMillis();
        int world = client.getWorld();
        if (world == lastSceneScanWorld && now - lastSceneScanAt < 10_000L)
        {
            return false;
        }
        lastSceneScanWorld = world;
        lastSceneScanAt = now;
        boolean changed = false;
        net.runelite.api.Tile[][][] tiles = client.getScene().getTiles();
        for (net.runelite.api.Tile[][] plane : tiles)
        {
            for (net.runelite.api.Tile[] column : plane)
            {
                for (net.runelite.api.Tile tile : column)
                {
                    if (tile == null) continue;
                    for (GameObject object : tile.getGameObjects())
                    {
                        if (object != null && tierForObjectId(object.getId()) > 0)
                        {
                            changed |= observe(object);
                        }
                    }
                }
            }
        }
        return changed;
    }


    public boolean onGameObjectDespawned(GameObjectDespawned event)
    {
        GameObject object = event.getGameObject();
        int tier = tierForObjectId(object.getId());
        if (tier <= 0 && !isCrashedStar(object)) return false;
        ShootingStarSighting sighting = sightings.get(client.getWorld());
        if (sighting != null && sighting.getPoint().equals(object.getWorldLocation()))
        {
            sighting.noLongerVisible();
            return true;
        }
        return false;
    }

    public List<ShootingStarSighting> sightings()
    {
        long cutoff = System.currentTimeMillis() - KEEP_SIGHTING_MS;
        sightings.values().removeIf(sighting -> sighting.getLastSeenAt() < cutoff);
        List<ShootingStarSighting> result = new ArrayList<>(sightings.values());
        result.sort(Comparator
            .comparingInt((ShootingStarSighting sighting) -> sighting.isActive() ? 0 : 1)
            .thenComparing(Comparator.comparingLong(ShootingStarSighting::getLastSeenAt).reversed()));
        return result;
    }

    private boolean observe(GameObject object)
    {
        int tier = tierForObjectId(object.getId());
        if (tier <= 0 && !isCrashedStar(object)) return false;
        WorldPoint point = object.getWorldLocation();
        int world = client.getWorld();
        long now = System.currentTimeMillis();
        ShootingStarSighting previous = sightings.get(world);
        boolean changed = previous == null || !previous.isActive()
            || !previous.getPoint().equals(point)
            || previous.getTier() != tier;
        if (previous != null && previous.getPoint().equals(point)
            && previous.getTier() == tier)
        {
            previous.seen(now);
        }
        else
        {
            sightings.put(world, new ShootingStarSighting(world, point,
                StarLocationCatalog.nearestName(point), tier, now));
        }
        return changed;
    }

    private boolean isCrashedStar(GameObject object)
    {
        ObjectComposition composition = client.getObjectDefinition(object.getId());
        if (composition == null || composition.getName() == null) return false;
        return composition.getName().toLowerCase().contains("crashed star");
    }

    static int tierForObjectId(int objectId)
    {
        for (int index = 0; index < TIER_IDS.length; index++)
        {
            if (TIER_IDS[index] == objectId) return index + 1;
        }
        return 0;
    }
}
