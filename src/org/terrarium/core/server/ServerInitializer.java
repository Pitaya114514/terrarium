package org.terrarium.core.server;

import org.terrarium.core.config.ConfigGroup;

public interface ServerInitializer {
    ConfigGroup[] init();
}
