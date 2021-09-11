package io.github.noeppi_noeppi.mods.bingolobby;

import io.github.noeppi_noeppi.libx.annotation.registration.RegisterClass;
import net.minecraft.data.worldgen.SurfaceBuilders;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraftforge.common.world.MobSpawnInfoBuilder;

import static net.minecraft.data.worldgen.biome.VanillaBiomes.calculateSkyColor;

@RegisterClass
public class ModBiomes {

    public static final Biome lobbyBiome = new Biome.BiomeBuilder()
            .scale(0.1f)
            .temperature(0.9f)
            .biomeCategory(Biome.BiomeCategory.NONE)
            .depth(0.2f)
            .precipitation(Biome.Precipitation.NONE)
            .downfall(1)
            .mobSpawnSettings(new MobSpawnInfoBuilder(MobSpawnSettings.EMPTY).build())
            .specialEffects(
                    new BiomeSpecialEffects.Builder()
                            .waterColor(0x43d5ee)
                            .waterFogColor(0x041f33)
                            .fogColor(0xc0d8ff)
                            .skyColor(calculateSkyColor(0.9f))
                            .build()
            ).generationSettings(
                    new BiomeGenerationSettings.Builder()
                            .surfaceBuilder(SurfaceBuilders.NOPE)
                            .build()
            ).build();
}
