package org.terrarium.core.config;

import java.util.Arrays;

public class ConfigGroup {
    private final String name;
    private final ConfigObject[] configObjects;
    private final String[] filename;

    public ConfigGroup(String name, ConfigObject... configObjects) {
        this.name = name;
        this.configObjects = configObjects;
        this.filename = new String[configObjects.length];
        for (int i = 0; i < configObjects.length; i++) {
            filename[i] = name + "." + configObjects[i].getName();
        }
    }

    public String getName() {
        return name;
    }

    public String[] getFilename() {
        return Arrays.copyOf(filename, filename.length);
    }

    public ConfigObject[] getConfigObjects() {
        return Arrays.copyOf(configObjects, configObjects.length);
    }

    @Override
    public String toString() {
        return name;
    }
}
