package io.github.noeppi_noeppi.mods.bingolobby.dimension;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.level.levelgen.structure.StructureSet;

public class StructureRegistryCodec {
    
    public static MapCodec<Registry<StructureSet>> fieldOf(String name) {
        return RegistryOps.retrieveRegistry(Registry.STRUCTURE_SET_REGISTRY);
    }
}
