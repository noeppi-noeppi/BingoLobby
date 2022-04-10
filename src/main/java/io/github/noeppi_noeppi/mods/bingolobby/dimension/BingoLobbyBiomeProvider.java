package io.github.noeppi_noeppi.mods.bingolobby.dimension;

import com.mojang.serialization.Codec;
import io.github.noeppi_noeppi.libx.annotation.api.Codecs;
import io.github.noeppi_noeppi.libx.annotation.codec.Dynamic;
import io.github.noeppi_noeppi.libx.annotation.codec.PrimaryConstructor;
import io.github.noeppi_noeppi.mods.bingolobby.BingoLobby;
import io.github.noeppi_noeppi.mods.bingolobby.ModBiomes;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import java.util.Objects;

public class BingoLobbyBiomeProvider extends BiomeSource {

    public static final Codec<BingoLobbyBiomeProvider> CODEC = Codecs.get(BingoLobby.class, BingoLobbyBiomeProvider.class);

    public final Registry<Biome> biomeRegistry;
    
    @PrimaryConstructor
    public BingoLobbyBiomeProvider(@Dynamic(BiomeRegistryCodec.class) Registry<Biome> biomeRegistry) {
        super(Objects.requireNonNull(ForgeRegistries.BIOMES.getResourceKey(ModBiomes.lobbyBiome), "Lobby biome not registered").flatMap(biomeRegistry::getHolder).stream());
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
    public Holder<Biome> getNoiseBiome(int x, int y, int z, @Nonnull Climate.Sampler sampler) {
        return this.biomeRegistry.getHolder(ResourceKey.create(Registry.BIOME_REGISTRY, Objects.requireNonNull(ModBiomes.lobbyBiome.getRegistryName(), "Bingo lobby biome has no registry name")))
                .orElseThrow(() -> new IllegalStateException("Bingo lobby biome not found in biome registry"));
    }
}
