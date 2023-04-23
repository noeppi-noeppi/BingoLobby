package io.github.noeppi_noeppi.mods.bingolobby.datagen;

import net.minecraft.core.Holder;
import net.minecraft.world.level.dimension.DimensionType;
import org.moddingx.libx.datagen.DatagenContext;
import org.moddingx.libx.datagen.provider.sandbox.DimensionTypeProviderBase;

public class BingoLobbyDimensionTypes extends DimensionTypeProviderBase {

    public final Holder<DimensionType> lobby = this.dimension()
            .height(0, 256)
            .ambientLight(3)
            .respawnDevices(false, false)
            .disableRaids()
            .piglinSafe()
            .monsterSpawnRule(0, 0)
            .build();
    
    public BingoLobbyDimensionTypes(DatagenContext ctx) {
        super(ctx);
    }
}
