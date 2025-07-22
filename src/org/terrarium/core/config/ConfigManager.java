package org.terrarium.core.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Properties;

public class ConfigManager {
    private static final Logger LOGGER = LogManager.getLogger(ConfigManager.class);
    private final String filename;
    private final String description;
    private final ConfigGroup[] configGroups;
    private final Properties properties;

    public ConfigManager(String filename, String description, ConfigGroup... configGroups) {
        this.filename = filename + ".properties";
        this.description = description;
        this.properties = new Properties();
        this.configGroups = configGroups;

        for (ConfigGroup configGroup : configGroups) {
            for (ConfigObject configObject : configGroup.getConfigObjects()) {
                properties.setProperty(configGroup.getName() + "." + configObject.getName(), configObject.getValue());
            }
        }
    }

    public String find(String groupName, String objectName) {
        for (ConfigGroup configGroup : configGroups) {
            if (configGroup.getName().equals(groupName)) {
                for (ConfigObject configObject : configGroup.getConfigObjects()) {
                    if (configObject.getName().equals(objectName)) {
                        return configObject.getValue();
                    }
                }
                return null;
            }
        }
        return null;
    }

    public void load() throws IOException {
        createDirectory();
        try (InputStream input = new FileInputStream(filename)) {
            properties.load(input);
        }
    }

    public void save() throws IOException {
        createDirectory();
        try (OutputStream output = new FileOutputStream(filename)) {
            properties.store(output, description);
        }
    }

    private void createDirectory() {
        String dir = "config";
        Path dirPath = Paths.get(dir);
        try {
            if (!Files.exists(dirPath)) {
                Files.createDirectory(dirPath);
            }
            Path filePath = Paths.get(dir, filename);
            if (!Files.exists(filePath)) {
                Files.createFile(filePath);
            }
        } catch (IOException e) {
            LOGGER.error("Unable to create directory: ", e);
        }
    }

    public String getFilename() {
        return filename;
    }

    public String getDescription() {
        return description;
    }

    public ConfigGroup[] getConfigGroups() {
        return Arrays.copyOf(configGroups, configGroups.length);
    }
}
