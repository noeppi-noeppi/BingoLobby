package io.github.noeppi_noeppi.mods.bingolobby.network;

import io.github.noeppi_noeppi.mods.bingolobby.Lobby;
import io.github.noeppi_noeppi.mods.bongo.network.BongoMessageType;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.PacketDistributor;
import org.moddingx.libx.mod.ModX;
import org.moddingx.libx.network.NetworkX;

public class LobbyNetwork extends NetworkX {

    public LobbyNetwork(ModX mod) {
        super(mod);
    }

    @Override
    protected void registerPackets() {
        this.registerGame(NetworkDirection.PLAY_TO_CLIENT, new LobbyUpdateMessage.Serializer(), () -> LobbyUpdateMessage.Handler::new);
    }

    @Override
    protected Protocol getProtocol() {
        return Protocol.of("2");
    }

    public void updateLobby(Level level) {
        if (!level.isClientSide) {
            this.channel.send(PacketDistributor.ALL.noArg(), new LobbyUpdateMessage(Lobby.get(level)));
        }
    }

    public void updateLobby(Player player) {
        if (!player.getCommandSenderWorld().isClientSide) {
            this.channel.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player), new LobbyUpdateMessage(Lobby.get(player.getCommandSenderWorld())));
        }
    }

    public void updateLobby(Level level, BongoMessageType messageType) {
        if (!level.isClientSide) {
            this.channel.send(PacketDistributor.ALL.noArg(), new LobbyUpdateMessage(Lobby.get(level), messageType));
        }
    }

    public void updateLobby(Player player, BongoMessageType messageType) {
        if (!player.getCommandSenderWorld().isClientSide) {
            this.channel.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player), new LobbyUpdateMessage(Lobby.get(player.getCommandSenderWorld()), messageType));
        }
    }
}
