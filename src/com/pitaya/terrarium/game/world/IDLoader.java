package com.pitaya.terrarium.game.world;

import com.pitaya.terrarium.game.effect.Effect;
import com.pitaya.terrarium.game.entity.Entity;
import com.pitaya.terrarium.game.item.Item;
import com.pitaya.terrarium.game.util.SubclassFinder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class IDLoader {
    private static final Logger LOGGER = LogManager.getLogger(IDLoader.class);
    private static IDLoader loader;

    public static IDLoader load() {
        loader = new IDLoader();
        return loader;
    }

    public static IDLoader getLoader() {
        return loader;
    }

    private final HashMap<Class<? extends Entity>, Short> entityIDMap = new HashMap<>();
    private final HashMap<Class<? extends Item>, Short> itemIDMap = new HashMap<>();
    private final HashMap<Class<? extends Effect>, Short> effectIDMap = new HashMap<>();

    private IDLoader() {
        SubclassFinder<Entity> entityClassFinder = new SubclassFinder<>();
        Set<Class<? extends Entity>> entityClasses = entityClassFinder.findSubclasses(Entity.class);
        short entityOrdinal = 1;
        for (Class<? extends Entity> subclass : entityClasses) {
            if (!Modifier.isAbstract(subclass.getModifiers())) {
                entityIDMap.put(subclass, entityOrdinal);
                entityOrdinal++;
            }
        }
        LOGGER.info("All entities has been initialized");

        SubclassFinder<Item> itemClassFinder = new SubclassFinder<>();
        Set<Class<? extends Item>> itemClasses = itemClassFinder.findSubclasses(Item.class);
        short itemOrdinal = 1;
        for (Class<? extends Item> subclass : itemClasses) {
            if (!Modifier.isAbstract(subclass.getModifiers())) {
                itemIDMap.put(subclass, itemOrdinal);
                itemOrdinal++;
            }
        }
        LOGGER.info("All items has been initialized");

        SubclassFinder<Effect> effectClassFinder = new SubclassFinder<>();
        Set<Class<? extends Effect>> effectClasses = effectClassFinder.findSubclasses(Effect.class);
        short effectOrdinal = 1;
        for (Class<? extends Effect> subclass : effectClasses) {
            if (!Modifier.isAbstract(subclass.getModifiers())) {
                effectIDMap.put(subclass, effectOrdinal);
                effectOrdinal++;
            }
        }
        LOGGER.info("All effects has been initialized");
    }

    public HashMap<Class<? extends Entity>, Short> getEntityIDMap() {
        return entityIDMap;
    }

    public Class<? extends Entity> findEntity(short id) {
        for (Map.Entry<Class<? extends Entity>, Short> classShortEntry : getEntityIDMap().entrySet()) {
            Class<? extends Entity> key = classShortEntry.getKey();
            Short value = classShortEntry.getValue();
            if (id == value) {
                return key;
            }
        }
        return null;
    }

    public HashMap<Class<? extends Item>, Short> getItemIDMap() {
        return itemIDMap;
    }

    public Class<? extends Item> findItem(short id) {
        for (Map.Entry<Class<? extends Item>, Short> classShortEntry : getItemIDMap().entrySet()) {
            Class<? extends Item> key = classShortEntry.getKey();
            Short value = classShortEntry.getValue();
            if (id == value) {
                return key;
            }
        }
        return null;
    }

    public HashMap<Class<? extends Effect>, Short> getEffectIDMap() {
        return effectIDMap;
    }

    public Class<? extends Effect> findEffect(short id) {
        for (Map.Entry<Class<? extends Effect>, Short> classShortEntry : getEffectIDMap().entrySet()) {
            Class<? extends Effect> key = classShortEntry.getKey();
            Short value = classShortEntry.getValue();
            if (id == value) {
                return key;
            }
        }
        return null;
    }
}
