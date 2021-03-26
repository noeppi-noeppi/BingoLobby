package io.github.noeppi_noeppi.mods.bingolobby.pregen;

import com.google.common.collect.ImmutableMap;
import io.github.noeppi_noeppi.mods.bingolobby.Lobby;
import io.github.noeppi_noeppi.mods.bingolobby.ModDimensions;
import io.github.noeppi_noeppi.mods.bongo.Bongo;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.Dimension;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.listener.IChunkStatusListener;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.DimensionSettings;
import net.minecraft.world.gen.settings.DimensionGeneratorSettings;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ChunkPregenerator {

    public static void pregenerateChunks(MinecraftServer server, long seed, int anvilRadius) {
        if (server instanceof FakeServer) {
            throw new IllegalStateException("Can't run bingo pregen task on a bingo pregen fake server.");
        }
        try {
            //int netherRadius = (int) Math.ceil(anvilRadius / 8d);
            int threads = Math.min(Runtime.getRuntime().availableProcessors() - 1, (2 * anvilRadius) * (2 * anvilRadius));
            int anvilsPerThreadOverworld = (int) Math.ceil(((double) (2 * anvilRadius) * (2 * anvilRadius)) / (double) threads);
            //int anvilsPerThreadNether = (int) Math.ceil(((double) (2 * netherRadius) * (2 * netherRadius)) / (double) threads);
            System.out.println("Pregen scheduled.\n" +
                    "Anvil files: Overworld: " + ((2 * anvilRadius) * (2 * anvilRadius)) + "\n" +
                    "Threads: " + threads + "\n" +
                    "Anvils per thread: Overworld: " + anvilsPerThreadOverworld);

            int overworldX = -anvilRadius;
            int overworldZ = -anvilRadius;

            /*int netherX = -netherRadius;
            int netherZ = -netherRadius;*/

            Path base = Files.createTempDirectory("bingo-pregen-");
            int localServerId = 0;
            List<PregenOptions> optionList = new ArrayList<>();
            for (int i = 0; i < threads; i++) {
                Path target = base.resolve(Integer.toString(localServerId++));
                List<AnvilCoordinates> overworldCoords = new ArrayList<>();
                //List<AnvilCoordinates> netherCoords = new ArrayList<>();
                for (int j = 0; j < anvilsPerThreadOverworld; j++) {
                    if (overworldX == Integer.MAX_VALUE || overworldZ == Integer.MAX_VALUE) {
                        break;
                    }
                    overworldCoords.add(new AnvilCoordinates(overworldX, overworldZ));
                    if (overworldX < (anvilRadius - 1)) {
                        overworldX += 1;
                    } else if (overworldZ < (anvilRadius - 1)) {
                        overworldX = -anvilRadius;
                        overworldZ += 1;
                    } else {
                        overworldX = Integer.MAX_VALUE;
                        overworldZ = Integer.MAX_VALUE;
                        break;
                    }
                }
                /*for (int j = 0; j < anvilsPerThreadNether; j++) {
                    if (netherX == Integer.MAX_VALUE || netherZ == Integer.MAX_VALUE) {
                        break;
                    }
                    netherCoords.add(new AnvilCoordinates(netherX, netherZ));
                    if (netherX < netherRadius) {
                        netherX += 1;
                    } else if (netherZ < netherRadius) {
                        netherX = -netherRadius;
                        netherZ += 1;
                    } else {
                        netherX = Integer.MAX_VALUE;
                        netherZ = Integer.MAX_VALUE;
                        break;
                    }
                }*/
                if (!overworldCoords.isEmpty()/* && !netherCoords.isEmpty()*/) {
                    optionList.add(new PregenOptions(seed, target, ImmutableMap.of(
                            World.OVERWORLD, overworldCoords
                            //World.THE_NETHER, netherCoords
                    )));
                }
            }
        
            Thread thread = new Thread(() -> {
                System.out.println("Starting " + optionList.size() + " pregen servers.");
                for (PregenOptions options : optionList) {
                    FakeServer.startFakeServer(server, options);
                }
                for (PregenOptions options : optionList) {
                    if (!options.waitForServer()) {
                        throw new IllegalStateException("Bingo pregen failed. Pregen server was not successful.");
                    }
                }
                System.out.println("All pregen servers are done. Assembling world");
                ServerPreTickQueue.schedulePreTickTask(server, () -> {
                    try {
                        System.out.println("Starting world assembly.");
                        System.out.println("Kicking all players.");
                        List<ServerPlayerEntity> playerList = new ArrayList<>(server.getPlayerList().getPlayers());
                        playerList.forEach(player -> player.connection.disconnect(new TranslationTextComponent("bingolobby.world_assembly.kick")));
                        System.out.println("Preserving Bingo and Lobby data");
                        // Bongo and Lobby objects are stored in the saves of the overworld. We need to preserve
                        // them as the old overworld is deleted.
                        CompoundNBT bongoNBT = Bongo.get(server.func_241755_D_()).write(new CompoundNBT());
                        CompoundNBT lobbyNBT = Lobby.get(server.func_241755_D_()).write(new CompoundNBT());
                        System.out.println("Unloading old worlds");
                        //noinspection deprecation
                        Map<RegistryKey<World>, ServerWorld> worldMap = server.forgeGetWorldMap();
                        ServerWorld lobbyWorld = null;
                        Path lobbyWorldPath = null;
                        Map<RegistryKey<World>, Pair<ServerWorld, Path>> worldsForDeletion = new HashMap<>();
                        for (ServerWorld world : worldMap.values()) {
                            if (ModDimensions.LOBBY_DIMENSION.equals(world.getDimensionKey())) {
                                lobbyWorld = world;
                                lobbyWorldPath = world.getChunkProvider().chunkManager.dimensionDirectory.toPath();
                            } else {
                                worldsForDeletion.put(world.getDimensionKey(), Pair.of(world, world.getChunkProvider().chunkManager.dimensionDirectory.toPath()));
                                MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.world.WorldEvent.Unload(world));
                                world.close();
                            }
                        }
                        worldMap.clear();
                        System.out.println("Deleting old worlds");
                        for (Map.Entry<RegistryKey<World>, Pair<ServerWorld, Path>> entry : worldsForDeletion.entrySet()) {
                            System.out.println("Deleting world of type " + entry.getKey().getRegistryName() + "/" + entry.getKey().getLocation() + " at " + entry.getValue().getRight().toAbsolutePath().normalize().toString());
                            Path effectiveFinalLobbyWorldPath = lobbyWorldPath;
                            Files.walkFileTree(entry.getValue().getRight(), new FileVisitor<Path>() {
                                @Override
                                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                                    if (effectiveFinalLobbyWorldPath != null && dir.toAbsolutePath().normalize().equals(effectiveFinalLobbyWorldPath.toAbsolutePath().normalize())) {
                                        return FileVisitResult.SKIP_SUBTREE;
                                    }
                                    return FileVisitResult.CONTINUE;
                                }

                                @Override
                                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                                    if (!Files.isDirectory(file)) {
                                        Files.delete(file);
                                    }
                                    return FileVisitResult.CONTINUE;
                                }

                                @Override
                                public FileVisitResult visitFileFailed(Path file, IOException exc) {
                                    return FileVisitResult.CONTINUE;
                                }

                                @Override
                                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                                    if (effectiveFinalLobbyWorldPath == null || !dir.toAbsolutePath().normalize().equals(effectiveFinalLobbyWorldPath.toAbsolutePath().normalize())) {
                                        if (Files.list(dir).count() == 0) {
                                            Files.delete(dir);
                                        }
                                    }
                                    return FileVisitResult.CONTINUE;
                                }
                            });
                        }
                        System.out.println("Copying pregenerated world data");
                        for (Map.Entry<RegistryKey<World>, Pair<ServerWorld, Path>> entry : worldsForDeletion.entrySet()) {
                            Path targetRegionPath = entry.getValue().getRight().resolve("region");
                            if (!Files.isDirectory(targetRegionPath)) {
                                Files.createDirectories(targetRegionPath);
                            }
                            for (PregenOptions options: optionList) {
                                Map<RegistryKey<World>, Path> pregenMap = options.getWorldPathMap();
                                if (pregenMap.containsKey(entry.getKey())) {
                                    System.out.println("Copying chunks from one pregen server for world " + entry.getKey().getRegistryName() + "/" + entry.getKey().getLocation());
                                    Path pregenRegionPath = pregenMap.get(entry.getKey()).resolve("region");
                                    List<Path> filesToCopy = Files.list(pregenRegionPath).filter(p -> options.isRegionFile(entry.getKey(), p)).collect(Collectors.toList());
                                    // No forEach because the stream is lazy and so it's loaded first
                                    for (Path fileToCopy : filesToCopy) {
                                        Path targetFile = targetRegionPath.resolve(fileToCopy.getFileName());
                                        Files.copy(fileToCopy, targetFile, StandardCopyOption.REPLACE_EXISTING);
                                    }
                                }
                            }
                        }
                        System.out.println("Generating new worlds");
                        DimensionGeneratorSettings generatorSettings = server.getServerConfiguration().getDimensionGeneratorSettings();
                        SimpleRegistry<Dimension> dimensionRegistry = generatorSettings.func_236224_e_();
                        IChunkStatusListener listener = server.chunkStatusListenerFactory.create(11);
                        for (Map.Entry<RegistryKey<World>, Pair<ServerWorld, Path>> entry : worldsForDeletion.entrySet()) {
                            System.out.println("Generating world " + entry.getKey().getRegistryName() + "/" + entry.getKey().getLocation());
                            RegistryKey<Dimension> dimensionKey = RegistryKey.getOrCreateKey(Registry.DIMENSION_KEY, entry.getKey().getLocation());
                            Dimension dimension = dimensionRegistry.getValueForKey(dimensionKey);
                            DimensionType dimensionType;
                            if (dimension == null) {
                                System.err.println("Warning: No dimension info found wor world type " + entry.getKey().getRegistryName() + "/" + entry.getKey().getLocation());
                                dimensionType = entry.getValue().getLeft().getDimensionType();
                            } else {
                                dimensionType = dimension.getDimensionType();
                            }
                            DynamicRegistries dynamicRegistries = server.field_240767_f_;
                            Registry<Biome> biomeTypeRegistry = dynamicRegistries.getRegistry(Registry.BIOME_KEY);
                            Registry<DimensionSettings> dimensionSettingRegistry = dynamicRegistries.getRegistry(Registry.NOISE_SETTINGS_KEY);
                            ChunkGenerator generator = DimensionGeneratorSettings.func_242750_a(biomeTypeRegistry, dimensionSettingRegistry, seed);
                            ServerWorld newWorld = new ServerWorld(server, server.backgroundExecutor, server.anvilConverterForAnvilFile,
                                    entry.getValue().getLeft().field_241103_E_, entry.getKey(), dimensionType, listener, generator,
                                    entry.getValue().getLeft().isDebug(), seed, entry.getValue().getLeft().field_241104_N_,
                                    entry.getValue().getLeft().field_241107_Q_);
                            worldMap.put(entry.getKey(), newWorld);
                        }
                        if (lobbyWorld != null) {
                            System.out.println("Restoring lobby word");
                            worldMap.put(ModDimensions.LOBBY_DIMENSION, lobbyWorld);
                        }
                        System.out.println("Restoring Bingo and Lobby data");
                        Bongo bongo = Bongo.get(server.func_241755_D_());
                        bongo.read(bongoNBT);
                        bongo.markDirty();
                        Lobby lobby = Lobby.get(server.func_241755_D_());
                        lobby.read(lobbyNBT);
                        lobby.markDirty();
                        System.out.println("Done. Closing server.");
                        
                    } catch (IOException e) {
                        System.out.println("Error assembling world. Closing server.");
                        e.printStackTrace();
                        server.serverRunning = false;
                        return true; // Skip tick
                    }
                    server.serverRunning = false;
                    return false; // Don't skip tick
                });
            });
            
            thread.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
