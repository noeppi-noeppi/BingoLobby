package io.github.noeppi_noeppi.mods.bingolobby;

import io.github.noeppi_noeppi.mods.bingolobby.dimension.BingoLobbyGenerator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

public class ModDimensions {

    public static final ResourceKey<Level> LOBBY_DIMENSION = ResourceKey.create(Registries.DIMENSION, new ResourceLocation(BingoLobby.getInstance().modid, "lobby"));

    public static void init() {
        BingoLobby.getInstance().register(Registries.CHUNK_GENERATOR, "lobby_generator", BingoLobbyGenerator.CODEC);
    }
    
    public static void teleportToLobby(ServerPlayer player, boolean yp) {
        player.getInventory().clearContent();
        if (player.level().dimension().equals(ModDimensions.LOBBY_DIMENSION)) {
            player.teleportTo(0.5, 65, 0.5);
            player.moveTo(0.5, 65, 0.5, 0, 0);
        } else {
            ServerLevel destination = player.serverLevel().getServer().getLevel(ModDimensions.LOBBY_DIMENSION);
            if (destination == null) {
                player.connection.disconnect(Component.literal("BingoLobby failed to load. Please restart server."));
            } else {
                float yRot = yp ? 0 : player.getYRot();
                float xRot = yp ? 0 : player.getXRot();
                player.teleportTo(
                        destination,
                        0.5, 65, 0.5,
                        yRot, xRot
                );
            }
        }
        player.setRespawnPosition(ModDimensions.LOBBY_DIMENSION, new BlockPos(0, 65, 0), 0, true, false);
    }
}
