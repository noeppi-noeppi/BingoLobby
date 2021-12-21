package io.github.noeppi_noeppi.mods.bingolobby;

import io.github.noeppi_noeppi.libx.annotation.registration.RegisterClass;
import net.minecraft.data.worldgen.biome.OverworldBiomes;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.MobSpawnSettings;

@RegisterClass
public class ModBiomes {

    public static final Biome lobbyBiome = new Biome.BiomeBuilder()
            .temperature(0.9f)
            .biomeCategory(Biome.BiomeCategory.NONE)
            .precipitation(Biome.Precipitation.NONE)
            .downfall(1)
            .mobSpawnSettings(new MobSpawnSettings.Builder().build())
            .specialEffects(
                    new BiomeSpecialEffects.Builder()
                            .waterColor(0x43d5ee)
                            .waterFogColor(0x041f33)
                            .fogColor(0xc0d8ff)
                            .skyColor(OverworldBiomes.calculateSkyColor(0.9f))
                            .build()
            )
            .generationSettings(new BiomeGenerationSettings.Builder().build())
            .build();
}
