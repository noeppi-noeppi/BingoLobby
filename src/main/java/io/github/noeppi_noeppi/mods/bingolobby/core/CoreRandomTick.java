package io.github.noeppi_noeppi.mods.bingolobby.core;

import io.github.noeppi_noeppi.mods.bingolobby.ModDimensions;
import net.minecraft.world.level.Level;

public class CoreRandomTick {
    
    public static boolean randomTick(Level level) {
        return level.dimension().equals(ModDimensions.LOBBY_DIMENSION);
    }
}
