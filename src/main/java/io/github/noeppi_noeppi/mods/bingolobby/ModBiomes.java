package io.github.noeppi_noeppi.mods.bingolobby;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeAmbience;
import net.minecraft.world.biome.BiomeGenerationSettings;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraft.world.gen.surfacebuilders.ConfiguredSurfaceBuilders;
import net.minecraftforge.common.world.MobSpawnInfoBuilder;

import static net.minecraft.world.biome.BiomeMaker.getSkyColorWithTemperatureModifier;


public class ModBiomes {

    public static final Biome lobbyBiome = new Biome.Builder()
            .scale(0.1f)
            .temperature(0.9f)
            .category(Biome.Category.NONE)
            .depth(0.2f)
            .precipitation(Biome.RainType.NONE)
            .downfall(1)
            .withMobSpawnSettings(new MobSpawnInfoBuilder(MobSpawnInfo.EMPTY).copy())
            .setEffects(
                    new BiomeAmbience.Builder()
                            .setWaterColor(0x43d5ee)
                            .setWaterFogColor(0x041f33)
                            .setFogColor(0xc0d8ff)
                            .withSkyColor(getSkyColorWithTemperatureModifier(0.9f))
                            .build()
            ).withGenerationSettings(
                    new BiomeGenerationSettings.Builder()
                            .withSurfaceBuilder(ConfiguredSurfaceBuilders.field_244184_p /* NOPE */)
                            .build()
            ).build();
    
    public static void init() {
        BingoLobby.getInstance().register("lobby", lobbyBiome);
    }
}
