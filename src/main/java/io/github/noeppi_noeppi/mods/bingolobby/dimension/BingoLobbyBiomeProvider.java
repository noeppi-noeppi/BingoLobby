package io.github.noeppi_noeppi.mods.bingolobby.dimension;

import com.mojang.serialization.Codec;
import io.github.noeppi_noeppi.mods.bingolobby.BingoLobby;
import io.github.noeppi_noeppi.mods.bingolobby.ModBiomes;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;
import org.moddingx.libx.annotation.api.Codecs;
import org.moddingx.libx.annotation.codec.Dynamic;
import org.moddingx.libx.annotation.codec.PrimaryConstructor;

import javax.annotation.Nonnull;
import java.util.List;

public class BingoLobbyBiomeProvider extends BiomeSource {

    public static final Codec<BingoLobbyBiomeProvider> CODEC = Codecs.get(BingoLobby.class, BingoLobbyBiomeProvider.class);

    public final Registry<Biome> biomeRegistry;
    
    @PrimaryConstructor
    public BingoLobbyBiomeProvider(@Dynamic(BiomeRegistryCodec.class) Registry<Biome> biomeRegistry) {
        super(List.of(ModBiomes.lobbyBiome));
        this.biomeRegistry = biomeRegistry;
    }
    
    @Nonnull
    @Override
    protected Codec<? extends BiomeSource> codec() {
        return CODEC;
    }

    @Nonnull
    @Override
    public Holder<Biome> getNoiseBiome(int x, int y, int z, @Nonnull Climate.Sampler sampler) {
        return ModBiomes.lobbyBiome;
    }
}
