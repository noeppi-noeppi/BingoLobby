package io.github.noeppi_noeppi.mods.bingolobby.pregen;

import com.mojang.serialization.Lifecycle;
import net.minecraft.util.datafix.codec.DatapackCodec;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.world.storage.ServerWorldInfo;

public class FakeServerConfiguration extends ServerWorldInfo {
    
    public FakeServerConfiguration(String world, PregenOptions options, DatapackCodec datapacks, DynamicRegistries registries) {
        super(options.getWorldSettings(world, datapacks), options.getGeneratorSettings(registries), Lifecycle.stable());
    }
}
