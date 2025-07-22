package org.terrarium.base;

import org.terrarium.core.config.ConfigGroup;
import org.terrarium.core.config.ConfigObject;
import org.terrarium.core.server.ServerInitializer;

public class BaseServerInitializer implements ServerInitializer {
    @Override
    public ConfigGroup[] init() {
        return new ConfigGroup[]{
                new ConfigGroup("server",
                        new ConfigObject("server-port", "25565", ConfigObject.ValueType.INTEGER),
                        new ConfigObject("server-ip", "127.0.0.1", ConfigObject.ValueType.STRING),
                        new ConfigObject("console-mode", "false", ConfigObject.ValueType.BOOLEAN)
                ),
        };
    }
}
