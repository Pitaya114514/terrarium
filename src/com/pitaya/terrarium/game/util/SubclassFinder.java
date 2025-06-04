package com.pitaya.terrarium.game.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

public class SubclassFinder<T> {
    private static final Logger LOGGER = LogManager.getLogger(SubclassFinder.class);

    public Set<Class<? extends T>> findSubclasses(Class<T> parentClass) {
        Set<Class<? extends T>> subclasses = new HashSet<>();
        String packageName = parentClass.getPackage().getName();

        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            String path = packageName.replace('.', '/');
            URL resource = classLoader.getResource(path);
            File directory;
            if (resource != null) {
                directory = new File(resource.getFile());
                if (directory.exists()) {
                    scanDirectoryForSubclasses(directory, packageName, parentClass, subclasses);
                }
            }
        } catch (Exception e) {
            LOGGER.warn(e);
        }
        return subclasses;
    }

    private void scanDirectoryForSubclasses(File directory, String packageName, Class<T> parentClass, Set<Class<? extends T>> subclasses) {
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                scanDirectoryForSubclasses(file, packageName + "." + file.getName(),
                        parentClass, subclasses);
            } else if (file.getName().endsWith(".class")) {
                String className = packageName + '.' +
                        file.getName().substring(0, file.getName().length() - 6);
                try {
                    Class<T> clazz = (Class<T>) Class.forName(className);
                    if (parentClass.isAssignableFrom(clazz) && !clazz.equals(parentClass)) {
                        subclasses.add(clazz);
                    }
                } catch (ClassNotFoundException e) {
                    LOGGER.warn(e);
                }
            }
        }
    }
}