package io.github.noeppi_noeppi.mods.bingolobby.mixin;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Random;

@Mixin(ConfiguredFeature.class)
public class MixinConfiguredFeature {

    @Final
    @Shadow
    public Feature<?> feature;
    
    @Final
    @Shadow
    public IFeatureConfig config;

    /**
     * @author BingoLobby
     * @reason Required to synchronise on feature configs to prevent a crash.
     */
    @Overwrite
    public boolean generate(ISeedReader reader, ChunkGenerator chunkGenerator, Random rand, BlockPos pos) {
        //noinspection SynchronizeOnNonFinalField
        synchronized (this.config) {
            //noinspection unchecked
            return ((Feature<IFeatureConfig>) this.feature).generate(reader, chunkGenerator, rand, pos, this.config);
        }
    }
}
