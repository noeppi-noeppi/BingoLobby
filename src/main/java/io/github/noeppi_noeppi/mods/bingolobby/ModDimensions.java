package io.github.noeppi_noeppi.mods.bingolobby;

import io.github.noeppi_noeppi.mods.bingolobby.dimension.BingoLobbyBiomeProvider;
import io.github.noeppi_noeppi.mods.bingolobby.dimension.BingoLobbyGenerator;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class ModDimensions {

    public static final RegistryKey<World> LOBBY_DIMENSION = RegistryKey.getOrCreateKey(Registry.WORLD_KEY, new ResourceLocation(BingoLobby.getInstance().modid, "lobby"));

    public static void init() {
        Registry.register(Registry.CHUNK_GENERATOR_CODEC, new ResourceLocation(BingoLobby.getInstance().modid, "lobby_generator"), BingoLobbyGenerator.CODEC);
        Registry.register(Registry.BIOME_PROVIDER_CODEC, new ResourceLocation(BingoLobby.getInstance().modid, "lobby_biome"), BingoLobbyBiomeProvider.CODEC);
    }
    
    public static void teleportToLobby(ServerPlayerEntity player, boolean yp) {
        player.inventory.clear();
        if (player.world.getDimensionKey().equals(ModDimensions.LOBBY_DIMENSION)) {
            player.setPositionAndUpdate(0.5, 65, 0.5);
            player.setLocationAndAngles(0.5, 65, 0.5, 0, 0);
        } else {
            ServerWorld destination = player.getServerWorld().getServer().getWorld(ModDimensions.LOBBY_DIMENSION);
            if (destination == null) {
                player.connection.disconnect(new StringTextComponent("Please restart server. A vanilla bug prevented the lobby to load."));
            } else {
                float yaw = yp ? 0 : player.rotationYaw;
                float pitch = yp ? 0 : player.rotationPitch;
                player.teleport(
                        destination,
                        0.5, 65, 0.5,
                        yaw, pitch
                );
            }
        }
        player.func_242111_a(ModDimensions.LOBBY_DIMENSION, new BlockPos(0, 65, 0), 0, true, false);
    }
}
