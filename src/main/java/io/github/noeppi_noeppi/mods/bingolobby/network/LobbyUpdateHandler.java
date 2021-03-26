package io.github.noeppi_noeppi.mods.bingolobby.network;

import io.github.noeppi_noeppi.mods.bingolobby.Lobby;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class LobbyUpdateHandler {
    
    public static void handle(LobbyUpdateSerializer.LobbyUpdateMessage msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> Lobby.updateClient(msg.lobby, msg.bongoMessageType));
        ctx.get().setPacketHandled(true);
    }
}
