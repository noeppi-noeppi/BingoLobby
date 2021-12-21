package io.github.noeppi_noeppi.mods.bingolobby.dimension;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import io.github.noeppi_noeppi.libx.annotation.api.Codecs;
import io.github.noeppi_noeppi.libx.annotation.codec.PrimaryConstructor;
import io.github.noeppi_noeppi.mods.bingolobby.BingoLobby;
import io.github.noeppi_noeppi.mods.bingolobby.ModBiomes;
import net.minecraft.core.Registry;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;

import javax.annotation.Nonnull;

public class BingoLobbyBiomeProvider extends BiomeSource {

    public static final Codec<BingoLobbyBiomeProvider> CODEC = Codecs.get(BingoLobby.class, BingoLobbyBiomeProvider.class);

    public final Registry<Biome> biomeRegistry;
    
    @PrimaryConstructor
    public BingoLobbyBiomeProvider(Registry<Biome> biomeRegistry) {
        super(ImmutableList.of(ModBiomes.lobbyBiome));
        this.biomeRegistry = biomeRegistry;
    }
    
    @Nonnull
    @Override
    protected Codec<? extends BiomeSource> codec() {
        return CODEC;
    }

    @Override
    @Nonnull
    public BiomeSource withSeed(long seed) {
        return new BingoLobbyBiomeProvider(this.biomeRegistry);
    }

    @Nonnull
    @Override
    public Biome getNoiseBiome(int x, int y, int z, @Nonnull Climate.Sampler sampler) {
        //noinspection ConstantConditions
        return this.biomeRegistry.get(ModBiomes.lobbyBiome.getRegistryName());
    }
}
