package io.github.noeppi_noeppi.mods.bingolobby.datagen;

import com.mojang.serialization.Lifecycle;
import io.github.noeppi_noeppi.mods.bingolobby.ModDimensions;
import io.github.noeppi_noeppi.mods.bingolobby.dimension.BingoLobbyGenerator;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.FixedBiomeSource;
import net.minecraft.world.level.dimension.LevelStem;
import org.moddingx.libx.datagen.DatagenContext;
import org.moddingx.libx.datagen.provider.sandbox.DimensionProviderBase;

public class BingoLobbyDimensions extends DimensionProviderBase {

    private final BingoLobbyDimensionTypes dimensionTypes = this.context.findRegistryProvider(BingoLobbyDimensionTypes.class);
    private final BingoLobbyBiomes biomes = this.context.findRegistryProvider(BingoLobbyBiomes.class);
    
    public final Holder<LevelStem> lobby = this.dimension(this.dimensionTypes.lobby, new BingoLobbyGenerator(new FixedBiomeSource(this.biomes.lobby)));
    
    public BingoLobbyDimensions(DatagenContext ctx) {
        super(ctx);
    }

    @Override
    public void run() {
        this.registries.writableRegistry(Registries.LEVEL_STEM).register(ResourceKey.create(Registries.LEVEL_STEM, ModDimensions.LOBBY_DIMENSION.location()), this.lobby.get(), Lifecycle.stable());
        super.run();
    }
}
