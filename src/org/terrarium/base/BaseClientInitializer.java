package org.terrarium.base;

import org.terrarium.core.client.ClientInitializer;
import org.terrarium.core.config.ConfigGroup;
import org.terrarium.core.config.ConfigObject;

public class BaseClientInitializer implements ClientInitializer {
    @Override
    public ConfigGroup[] init() {
        return new ConfigGroup[]{
                new ConfigGroup("client",
                        new ConfigObject("debug-mode", "false", ConfigObject.ValueType.BOOLEAN),
                        new ConfigObject("smooth-camara", "false", ConfigObject.ValueType.BOOLEAN),
                        new ConfigObject("game-window-width", "1600", ConfigObject.ValueType.INTEGER),
                        new ConfigObject("game-window-height", "900", ConfigObject.ValueType.INTEGER)
                ),
                new ConfigGroup("game", new ConfigObject("fsfiod", "423", ConfigObject.ValueType.INTEGER))
        };
    }
}
