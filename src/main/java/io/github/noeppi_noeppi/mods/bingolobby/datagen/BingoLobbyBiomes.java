package io.github.noeppi_noeppi.mods.bingolobby.datagen;

import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import org.moddingx.libx.datagen.DatagenContext;
import org.moddingx.libx.datagen.provider.sandbox.BiomeProviderBase;

public class BingoLobbyBiomes extends BiomeProviderBase {

    public final Holder<Biome> lobby = this.biome(0.9f, 1)
            .mobSpawns(this.spawns())
            .effects(this.effects()
                    .waterColor(0x43d5ee)
                    .waterFogColor(0x041f33)
                    .fogColor(0xc0d8ff)
            )
            .generation(this.generation())
            .build();
            
    public BingoLobbyBiomes(DatagenContext ctx) {
        super(ctx);
    }
}
