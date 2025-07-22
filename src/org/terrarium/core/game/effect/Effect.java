package org.terrarium.core.game.effect;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.terrarium.core.game.block.Block;

public final class Effect {
    private static final Logger LOGGER = LogManager.getLogger(Effect.class);

    @Override
    public Effect clone() {
        try {
            return (Effect) super.clone();
        } catch (CloneNotSupportedException e) {
            LOGGER.error("Unable to clone {}", this, e);
            return null;
        }
    }


}
