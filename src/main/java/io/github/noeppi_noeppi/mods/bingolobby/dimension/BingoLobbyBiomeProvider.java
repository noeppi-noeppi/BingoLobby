package io.github.noeppi_noeppi.mods.bingolobby.dimension;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.noeppi_noeppi.mods.bingolobby.ModBiomes;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryLookupCodec;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

public class BingoLobbyBiomeProvider extends BiomeProvider {

    public static final Codec<BingoLobbyBiomeProvider> CODEC =
            RecordCodecBuilder.create((instance) -> instance.group(
                    RegistryLookupCodec.getLookUpCodec(Registry.BIOME_KEY)
                            .forGetter(provider -> provider.biomeRegistry))
            .apply(instance, instance.stable(BingoLobbyBiomeProvider::new)));

    private final Registry<Biome> biomeRegistry;
    
    public BingoLobbyBiomeProvider(Registry<Biome> biomeRegistry) {
        super(ImmutableList.of(ModBiomes.lobbyBiome));
        this.biomeRegistry = biomeRegistry;
    }
    
    @Nonnull
    @Override
    protected Codec<? extends BiomeProvider> getBiomeProviderCodec() {
        return CODEC;
    }

    @Override
    @Nonnull
    @OnlyIn(Dist.CLIENT)
    public BiomeProvider getBiomeProvider(long seed) {
        return new BingoLobbyBiomeProvider(this.biomeRegistry);
    }

    @Nonnull
    @Override
    public Biome getNoiseBiome(int x, int y, int z) {
        //noinspection ConstantConditions
        return this.biomeRegistry.getOrDefault(ModBiomes.lobbyBiome.getRegistryName());
    }
}
