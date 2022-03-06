package io.github.noeppi_noeppi.mods.bingolobby.dimension;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.structure.StructureSet;

public class BiomeRegistryCodec {
    
    public static MapCodec<Registry<Biome>> fieldOf(String name) {
        return RegistryOps.retrieveRegistry(Registry.BIOME_REGISTRY);
    }
}
