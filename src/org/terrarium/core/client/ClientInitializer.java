package org.terrarium.core.client;

import org.terrarium.core.config.ConfigGroup;

public interface ClientInitializer {
    ConfigGroup[] init();
}
