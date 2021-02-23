package io.github.noeppi_noeppi.mods.bingolobby.network;

import io.github.noeppi_noeppi.libx.mod.ModX;
import io.github.noeppi_noeppi.libx.network.NetworkX;
import io.github.noeppi_noeppi.mods.bingolobby.Lobby;
import io.github.noeppi_noeppi.mods.bongo.network.BongoMessageType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.PacketDistributor;

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
        return "1";
    }

    public void updateLobby(World world) {
        if (!world.isRemote) {
            this.instance.send(PacketDistributor.ALL.noArg(), new LobbyUpdateSerializer.LobbyUpdateMessage(Lobby.get(world)));
        }
    }

    public void updateLobby(PlayerEntity player) {
        if (!player.getEntityWorld().isRemote) {
            this.instance.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new LobbyUpdateSerializer.LobbyUpdateMessage(Lobby.get(player.getEntityWorld())));
        }
    }

    public void updateLobby(World world, BongoMessageType messageType) {
        if (!world.isRemote) {
            this.instance.send(PacketDistributor.ALL.noArg(), new LobbyUpdateSerializer.LobbyUpdateMessage(Lobby.get(world), messageType));
        }
    }

    public void updateLobby(PlayerEntity player, BongoMessageType messageType) {
        if (!player.getEntityWorld().isRemote) {
            this.instance.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new LobbyUpdateSerializer.LobbyUpdateMessage(Lobby.get(player.getEntityWorld()), messageType));
        }
    }
}
