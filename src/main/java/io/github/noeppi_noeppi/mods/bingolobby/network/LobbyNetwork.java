package io.github.noeppi_noeppi.mods.bingolobby.network;

import io.github.noeppi_noeppi.libx.mod.ModX;
import io.github.noeppi_noeppi.libx.network.NetworkX;
import io.github.noeppi_noeppi.mods.bingolobby.Lobby;
import io.github.noeppi_noeppi.mods.bongo.network.BongoMessageType;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.fmllegacy.network.NetworkDirection;
import net.minecraftforge.fmllegacy.network.PacketDistributor;

public class LobbyNetwork extends NetworkX {

    public LobbyNetwork(ModX mod) {
        super(mod);
    }

    @Override
    protected void registerPackets() {
        this.register(new LobbyUpdateSerializer(), () -> LobbyUpdateHandler::handle, NetworkDirection.PLAY_TO_CLIENT);
    }

    @Override
    protected String getProtocolVersion() {
        return "2";
    }

    public void updateLobby(Level level) {
        if (!level.isClientSide) {
            this.instance.send(PacketDistributor.ALL.noArg(), new LobbyUpdateSerializer.LobbyUpdateMessage(Lobby.get(level)));
        }
    }

    public void updateLobby(Player player) {
        if (!player.getCommandSenderWorld().isClientSide) {
            this.instance.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player), new LobbyUpdateSerializer.LobbyUpdateMessage(Lobby.get(player.getCommandSenderWorld())));
        }
    }

    public void updateLobby(Level level, BongoMessageType messageType) {
        if (!level.isClientSide) {
            this.instance.send(PacketDistributor.ALL.noArg(), new LobbyUpdateSerializer.LobbyUpdateMessage(Lobby.get(level), messageType));
        }
    }

    public void updateLobby(Player player, BongoMessageType messageType) {
        if (!player.getCommandSenderWorld().isClientSide) {
            this.instance.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player), new LobbyUpdateSerializer.LobbyUpdateMessage(Lobby.get(player.getCommandSenderWorld()), messageType));
        }
    }
}
