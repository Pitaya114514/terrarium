package com.pitaya.terrarium.game.world;

import com.pitaya.terrarium.game.effect.Effect;
import com.pitaya.terrarium.game.entity.Entity;
import com.pitaya.terrarium.game.item.Item;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        try (ScanResult scanResult = new ClassGraph().enableClassInfo().scan()) {
            List<Class<?>> entityClasses = scanResult.getSubclasses(Entity.class).loadClasses();
            short eid = 0;
            for (Class<?> entityClass : entityClasses) {
                int modifiers = entityClass.getModifiers();
                if (!Modifier.isAbstract(modifiers) && !Modifier.isInterface(modifiers) && !entityClass.isEnum()) {
                    eid++;
                    entityIDMap.put((Class<? extends Entity>) entityClass, eid);
                }
            }
            LOGGER.info("All entities has been initialized");

            List<Class<?>> itemClasses = scanResult.getSubclasses(Item.class).loadClasses();
            short iid = 0;
            for (Class<?> itemClass : itemClasses) {
                int modifiers = itemClass.getModifiers();
                if (!Modifier.isAbstract(modifiers) && !Modifier.isInterface(modifiers) && !itemClass.isEnum()) {
                    iid++;
                    itemIDMap.put((Class<? extends Item>) itemClass, iid);
                }
            }
            LOGGER.info("All items has been initialized");

            List<Class<?>> effectClasses = scanResult.getSubclasses(Effect.class).loadClasses();
            short efid = 0;
            for (Class<?> effectClass : effectClasses) {
                int modifiers = effectClass.getModifiers();
                if (!Modifier.isAbstract(modifiers) && !Modifier.isInterface(modifiers) && !effectClass.isEnum()) {
                    efid++;
                    effectIDMap.put((Class<? extends Effect>) effectClass, efid);
                }
            }
            LOGGER.info("All effects has been initialized");
        }


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
