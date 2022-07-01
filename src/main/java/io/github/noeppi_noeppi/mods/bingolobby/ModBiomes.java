package io.github.noeppi_noeppi.mods.bingolobby;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.data.worldgen.biome.OverworldBiomes;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.MobSpawnSettings;

public class ModBiomes {

    public static final Holder<Biome> lobbyBiome = BingoLobby.getInstance().createHolder(Registry.BIOME_REGISTRY, "lobby_biome", new Biome.BiomeBuilder()
            .temperature(0.9f)
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
            .build());
    
    public static void init() {
        // load class
    }
}
