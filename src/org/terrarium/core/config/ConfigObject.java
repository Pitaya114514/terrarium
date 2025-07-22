package org.terrarium.core.config;

import java.util.function.Consumer;

public class ConfigObject {
    public enum ValueType {
        STRING, INTEGER, FLOAT, BOOLEAN
    }

    private final String name;
    private final Consumer<ConfigObject> applyAction;
    public final ValueType valueType;
    private String value;

    public ConfigObject(String name, String value, ValueType valueType, Consumer<ConfigObject> applyAction) {
        this.name = name;
        this.value = value;
        this.valueType = valueType;
        this.applyAction = applyAction;
    }

    public ConfigObject(String name, String value, ValueType valueType) {
        this(name, value, valueType, null);
    }

    public void apply(String value) {
        if (applyAction != null) {
            applyAction.accept(this);
        }
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }
}
