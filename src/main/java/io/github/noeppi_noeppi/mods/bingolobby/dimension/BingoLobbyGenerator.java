package io.github.noeppi_noeppi.mods.bingolobby.dimension;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.noeppi_noeppi.mods.bingolobby.config.LobbyConfig;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.Blockreader;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.BiomeManager;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.WorldGenRegion;
import net.minecraft.world.gen.feature.structure.StructureManager;
import net.minecraft.world.gen.settings.DimensionStructuresSettings;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

public class BingoLobbyGenerator extends ChunkGenerator {

    public static final Codec<BingoLobbyGenerator> CODEC = RecordCodecBuilder.create(
            (instance) -> instance.group(
                    BiomeProvider.CODEC.fieldOf("biome_source").forGetter(s -> s.biomeProvider)
            ).apply(instance, instance.stable(BingoLobbyGenerator::new)));

    private final BiomeProvider biomeProvider;

    private BingoLobbyGenerator(BiomeProvider biomeSource) {
        super(biomeSource, biomeSource, new DimensionStructuresSettings(false), 0);
        this.biomeProvider = biomeSource;
    }

    @Nonnull
    @Override
    protected Codec<? extends ChunkGenerator> getChunkGeneratorCodec() {
        return CODEC;
    }

    @Nonnull
    @Override
    @OnlyIn(Dist.CLIENT)
    public ChunkGenerator createForSeed(long seed) {
        return new BingoLobbyGenerator(this.biomeProvider);
    }

    @Override
    public void generateSurface(@Nonnull WorldGenRegion region, @Nonnull IChunk chunk) {
        ChunkPos cp = chunk.getPos();
        int xs = cp.getXStart();
        int zs = cp.getZStart();
        int xe = cp.getXEnd();
        int ze = cp.getZEnd();
        if (LobbyConfig.is_void) {
            if (xs <= 0 && xe >= 0 &&zs <= 0 &&ze >= 0) {
                chunk.setBlockState(new BlockPos(0, 64, 0), Blocks.DIAMOND_BLOCK.getDefaultState(), false);
            }
        } else {
            BlockPos.Mutable pos = new BlockPos.Mutable();
            BlockState bedrock = Blocks.BEDROCK.getDefaultState();
            BlockState stone = Blocks.STONE.getDefaultState();
            BlockState dirt = Blocks.DIRT.getDefaultState();
            BlockState grass = Blocks.GRASS_BLOCK.getDefaultState();
            BlockState diamond = Blocks.DIAMOND_BLOCK.getDefaultState();
            BlockState air = Blocks.AIR.getDefaultState();
            for (int x = xs; x <= xe; x++) {
                for (int z = zs; z <= ze; z++) {
                    pos.setX(x);
                    pos.setZ(z);

                    pos.setY(0);
                    chunk.setBlockState(pos, bedrock, false);

                    for (int y = 1; y <= 58; y++) {
                        pos.setY(y);
                        chunk.setBlockState(pos, stone, false);
                    }

                    for (int y = 59; y <= 63; y++) {
                        pos.setY(y);
                        chunk.setBlockState(pos, dirt, false);
                    }

                    pos.setY(64);
                    chunk.setBlockState(pos, x == 0 && z == 0 ? diamond : grass, false);

                    for (int y = 65; y < chunk.getHeight(); y++) {
                        pos.setY(y);
                        chunk.setBlockState(pos, air, false);
                    }
                }
            }
        }
    }

    @Override
    public void generateStructures(@Nonnull IWorld world, @Nonnull StructureManager structures, @Nonnull IChunk chunk) {
        //
    }

    @Override
    public int getHeight(int x, int z, @Nonnull Heightmap.Type heightmapType) {
        return 1;
    }

    @Nonnull
    @Override
    public IBlockReader func_230348_a_(int p_230348_1_, int p_230348_2_) {
        return new Blockreader(new BlockState[0]);
    }

    @Override
    public void generateCarvings(long seed, @Nonnull BiomeManager biomes, @Nonnull IChunk chunk, @Nonnull GenerationStage.Carving stage) {
        //
    }

    @Override
    public void generateFeatures(@Nonnull WorldGenRegion region, @Nonnull StructureManager structures) {
        //
    }
}
