package io.github.noeppi_noeppi.mods.bingolobby.dimension;

import com.mojang.serialization.Codec;
import io.github.noeppi_noeppi.libx.annotation.codec.Codecs;
import io.github.noeppi_noeppi.libx.annotation.codec.PrimaryConstructor;
import io.github.noeppi_noeppi.mods.bingolobby.BingoLobby;
import io.github.noeppi_noeppi.mods.bingolobby.config.LobbyConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.StructureSettings;

import javax.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class BingoLobbyGenerator extends ChunkGenerator {

    public static final Codec<BingoLobbyGenerator> CODEC = Codecs.get(BingoLobby.class, BingoLobbyGenerator.class);

    public final BiomeSource biomeSource;

    @PrimaryConstructor
    public BingoLobbyGenerator(BiomeSource biomeSource) {
        super(biomeSource, biomeSource, new StructureSettings(false), 0);
        this.biomeSource = biomeSource;
    }

    @Nonnull
    @Override
    protected Codec<? extends ChunkGenerator> codec() {
        return CODEC;
    }

    @Nonnull
    @Override
    public ChunkGenerator withSeed(long seed) {
        return new BingoLobbyGenerator(this.biomeSource);
    }

    @Override
    public void buildSurfaceAndBedrock(@Nonnull WorldGenRegion level, @Nonnull ChunkAccess chunk) {
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
    public CompletableFuture<ChunkAccess> fillFromNoise(@Nonnull Executor executor, @Nonnull StructureFeatureManager structures, @Nonnull ChunkAccess chunk) {
        return CompletableFuture.completedFuture(chunk);
    }

    @Override
    public int getBaseHeight(int x, int z, @Nonnull Heightmap.Types heightmap, @Nonnull LevelHeightAccessor level) {
        return 64;
    }

    @Nonnull
    @Override
    public NoiseColumn getBaseColumn(int i, int i1, @Nonnull LevelHeightAccessor levelHeightAccessor) {
        return new NoiseColumn(0, new BlockState[]{});
    }

    @Override
    public void applyCarvers(long seed, @Nonnull BiomeManager biomeManager, @Nonnull ChunkAccess chunk, @Nonnull GenerationStep.Carving carving) {
        //
    }

    @Override
    public void applyBiomeDecoration(@Nonnull WorldGenRegion region, @Nonnull StructureFeatureManager structureManager) {
        //
    }
}
