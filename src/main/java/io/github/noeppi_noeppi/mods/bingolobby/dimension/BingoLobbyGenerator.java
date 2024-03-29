package io.github.noeppi_noeppi.mods.bingolobby.dimension;

import com.mojang.serialization.Codec;
import io.github.noeppi_noeppi.mods.bingolobby.BingoLobby;
import io.github.noeppi_noeppi.mods.bingolobby.config.LobbyConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.*;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;
import org.moddingx.libx.annotation.api.Codecs;
import org.moddingx.libx.annotation.codec.PrimaryConstructor;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class BingoLobbyGenerator extends ChunkGenerator {

    public static final Codec<BingoLobbyGenerator> CODEC = Codecs.get(BingoLobby.class, BingoLobbyGenerator.class);

    public final BiomeSource biomeSource;

    @PrimaryConstructor
    public BingoLobbyGenerator(BiomeSource biomeSource) {
        super(biomeSource);
        this.biomeSource = biomeSource;
    }

    @Nonnull
    @Override
    protected Codec<? extends ChunkGenerator> codec() {
        return CODEC;
    }

    @Override
    public void buildSurface(@Nonnull WorldGenRegion level, @Nonnull StructureManager structures, @Nonnull RandomState random, @Nonnull ChunkAccess chunk) {
        ChunkPos cp = chunk.getPos();
        int xs = cp.getMinBlockX();
        int zs = cp.getMinBlockZ();
        int xe = cp.getMaxBlockX();
        int ze = cp.getMaxBlockZ();
        if (LobbyConfig.is_void) {
            if (xs <= 0 && xe >= 0 &&zs <= 0 &&ze >= 0) {
                chunk.setBlockState(new BlockPos(0, 64, 0), Blocks.DIAMOND_BLOCK.defaultBlockState(), false);
            }
        } else {
            BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
            BlockState bedrock = Blocks.BEDROCK.defaultBlockState();
            BlockState stone = Blocks.STONE.defaultBlockState();
            BlockState dirt = Blocks.DIRT.defaultBlockState();
            BlockState grass = Blocks.GRASS_BLOCK.defaultBlockState();
            BlockState diamond = Blocks.DIAMOND_BLOCK.defaultBlockState();
            BlockState air = Blocks.AIR.defaultBlockState();
            for (int x = xs; x <= xe; x++) {
                for (int z = zs; z <= ze; z++) {
                    pos.setX(x);
                    pos.setZ(z);
                    
                    pos.setY(chunk.getMinBuildHeight());
                    chunk.setBlockState(pos, bedrock, false);

                    for (int y = chunk.getMinBuildHeight() + 1; y <= 58; y++) {
                        pos.setY(y);
                        chunk.setBlockState(pos, stone, false);
                    }

                    for (int y = 59; y <= 63; y++) {
                        pos.setY(y);
                        chunk.setBlockState(pos, dirt, false);
                    }

                    pos.setY(64);
                    chunk.setBlockState(pos, x == 0 && z == 0 ? diamond : grass, false);

                    for (int y = 65; y < chunk.getMaxBuildHeight(); y++) {
                        pos.setY(y);
                        chunk.setBlockState(pos, air, false);
                    }
                }
            }
        }
    }
    
    @Nonnull
    @Override
    public CompletableFuture<ChunkAccess> fillFromNoise(@Nonnull Executor executor, @Nonnull Blender blender, @Nonnull RandomState random, @Nonnull StructureManager structures, @Nonnull ChunkAccess chunk) {
        return CompletableFuture.completedFuture(chunk);
    }

    @Override
    public int getBaseHeight(int x, int z, @Nonnull Heightmap.Types heightmap, @Nonnull LevelHeightAccessor level, @Nonnull RandomState random) {
        return 64;
    }

    @Nonnull
    @Override
    public NoiseColumn getBaseColumn(int i, int i1, @Nonnull LevelHeightAccessor levelHeightAccessor, @Nonnull RandomState random) {
        return new NoiseColumn(0, new BlockState[]{});
    }
    
    @Override
    public void addDebugScreenInfo(@Nonnull List<String> list, @Nonnull RandomState random, @Nonnull BlockPos pos) {
        //
    }

    @Override
    public void applyCarvers(@Nonnull WorldGenRegion level, long seed, @Nonnull RandomState random, @Nonnull BiomeManager biomes, @Nonnull StructureManager structures, @Nonnull ChunkAccess chunk, @Nonnull GenerationStep.Carving step) {
        //
    }

    @Override
    public void applyBiomeDecoration(@Nonnull WorldGenLevel level, @Nonnull ChunkAccess chunk, @Nonnull StructureManager structures) {
        //
    }

    @Override
    public void spawnOriginalMobs(@Nonnull WorldGenRegion level) {
        //
    }

    @Override
    public int getGenDepth() {
        return 384;
    }

    @Override
    public int getSeaLevel() {
        return 63;
    }

    @Override
    public int getMinY() {
        return 0;
    }
}
