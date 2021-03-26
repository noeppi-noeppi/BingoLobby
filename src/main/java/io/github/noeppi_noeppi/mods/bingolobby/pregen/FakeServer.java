package io.github.noeppi_noeppi.mods.bingolobby.pregen;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.Util;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraft.world.chunk.listener.IChunkStatusListener;
import net.minecraft.world.chunk.listener.LoggingChunkStatusListener;
import net.minecraft.world.server.ServerChunkProvider;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.net.Proxy;
import java.nio.file.Path;
import java.util.*;
import java.util.function.BooleanSupplier;

public class FakeServer extends MinecraftServer {

    private static int nextId = 0;
    
    @SuppressWarnings("FieldCanBeLocal")
    private final int id;
    private final PregenOptions options;
    
    public FakeServer(MinecraftServer parent, PregenOptions options, Thread serverThread) {
        super(serverThread, parent.field_240767_f_,
                options.getLevelSave("world"),
                new FakeServerConfiguration("world", options,
                        parent.getServerConfiguration().getDatapackCodec(),
                        parent.func_244267_aX()),
                parent.getResourcePacks(),
                Proxy.NO_PROXY,
                parent.getDataFixer(),
                options.getDataRegistries(),
                FakeMinecraftSessionService.INSTANCE,
                FakeMinecraftSessionService.INSTANCE,
                FakePlayerProfileCache.create(),
                LoggingChunkStatusListener::new);
        this.id = nextId++;
        this.options = options;
        ObfuscationReflectionHelper.setPrivateValue(MinecraftServer.class, this, Util.createNamedService("Bingo-Pregen-" + this.id), "field_213217_au");
        this.setServerId("Bingo-Pregen-" + this.id);
    }

    @Override
    protected boolean init() {
        System.out.println("Starting Pregen server " + this.id + " (" + this.options.getAnvilFiles().size() + " anvil files)");
        this.setOnlineMode(false);
        this.setServerPort(-1);
        this.func_244801_P();
        this.setResourcePackFromWorld();
        this.serverConfig.addServerBranding(this.getServerModName(), this.func_230045_q_().isPresent());
        this.setPlayerList(new FakePlayerList(this));
        IChunkStatusListener listener = this.chunkStatusListenerFactory.create(11);
        this.func_240787_a_(listener);

        Set<RegistryKey<World>> worlds = this.options.getWorlds();
        Map<RegistryKey<World>, Path> paths = new HashMap<>();
        for (RegistryKey<World> worldKey : worlds) {
            List<AnvilCoordinates> anvils = this.options.getAnvilFiles(worldKey);
            
            ServerWorld world = this.getWorld(worldKey);
            Objects.requireNonNull(world);
            for (int i = 0; i < anvils.size(); i++) {
                System.out.println("Generation of Anvil file " + (i + 1) + " / " + anvils.size() + " for world " + worldKey.getRegistryName() + "/" + worldKey.getLocation() +  " on server " + this.id + ".");
                ChunkPos[] chunks = anvils.get(i).getChunks();
                for (ChunkPos chunk : chunks) {
                    world.forceChunk(chunk.x, chunk.z, true);
                }
                for (ChunkPos chunk : chunks) {
                    world.forceChunk(chunk.x, chunk.z, false);
                }
                System.out.println("Save Anvil file " + (i + 1) + " / " + anvils.size() + " on server " + this.id + ".");
                ServerChunkProvider provider = world.getChunkProvider();
                world.saveLevel();
                provider.save(false);
            }
            paths.put(worldKey, world.getChunkProvider().chunkManager.dimensionDirectory.toPath());
        }
        
        this.options.setWorldPathMap(paths);
        
        // Do not really start server, just save chunks we have generated.
        return false;
    }

    
    
    @Override
    protected void tick(@Nonnull BooleanSupplier hasTimeLeft) {
        // We never tick the fake server
    }

    @Override
    protected void func_240802_v_() {
        try {
         if (this.init()) {
            this.serverTime = Util.milliTime();
             //noinspection ConstantConditions
             this.finalTick(null);
         }
      } catch (Throwable t) {
         //
      } finally {
         try {
            this.serverStopped = true;
            this.stopServer();
            this.options.notifyServerDone(true);
         } catch (Throwable t) {
             this.options.notifyServerDone(false);
             System.err.println("Exception stopping pregen server");
             t.printStackTrace();
         }
      }
    }

    @Override
    protected void stopServer() {
        System.out.println("Stopping bingo pregen server");
        if (this.getNetworkSystem() != null) {
            this.getNetworkSystem().terminateEndpoints();
        }

        //noinspection ConstantConditions
        if (this.getPlayerList() != null) {
            System.out.println("Stopping bingo pregen players");
            this.getPlayerList().saveAllPlayerData();
            this.getPlayerList().removeAllPlayers();
        }

        System.out.println("Saving bingo pregen world data");

        for(ServerWorld world : this.getWorlds()) {
            if (world != null) {
                world.disableLevelSaving = false;
            }
        }

        this.save(false, true, false);

        for(ServerWorld world : this.getWorlds()) {
            if (world != null) {
                try {
                    world.close();
                } catch (IOException e) {
                    System.err.println("Exception closing the bingo pregen level");
                    e.printStackTrace();
                }
            }
        }

        //this.getDataPackRegistries().close();

        try {
            this.anvilConverterForAnvilFile.close();
        } catch (IOException ioexception) {
            //
        }
        
        System.out.println("Bingo pregen server " + this.id + " is done");
    }

    @Override
    public int getOpPermissionLevel() {
        return 4;
    }

    @Override
    public int getFunctionLevel() {
        return 2;
    }

    @Override
    public boolean allowLoggingRcon() {
        return false;
    }

    @Nonnull
    @Override
    public Optional<String> func_230045_q_() {
        return Optional.of("YES THIS IS MODDED! Fake Server for Bingo Pregen!");
    }

    @Override
    public boolean isDedicatedServer() {
        return true;
    }

    @Override
    public int func_241871_k() {
        return 0;
    }

    @Override
    public boolean shouldUseNativeTransport() {
        return false;
    }

    @Override
    public boolean isCommandBlockEnabled() {
        return false;
    }

    @Override
    public boolean getPublic() {
        return false;
    }

    @Override
    public boolean shareToLAN(@Nonnull GameType gameMode, boolean cheats, int port) {
        return false;
    }

    @Override
    public boolean isServerOwner(@Nonnull GameProfile profileIn) {
        return false;
    }

    @Override
    public boolean allowLogging() {
        return false;
    }
    
    public static FakeServer startFakeServer(MinecraftServer parent, PregenOptions options) {
        return MinecraftServer.startServer(t -> new FakeServer(parent, options, t));
    }
}
