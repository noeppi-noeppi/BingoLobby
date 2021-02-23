package io.github.noeppi_noeppi.mods.bingolobby.network;

import io.github.noeppi_noeppi.libx.network.PacketSerializer;
import io.github.noeppi_noeppi.mods.bingolobby.Lobby;
import io.github.noeppi_noeppi.mods.bongo.network.BongoMessageType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;

import java.util.Objects;

public class LobbyUpdateSerializer implements PacketSerializer<LobbyUpdateSerializer.LobbyUpdateMessage> {

    @Override
    public Class<LobbyUpdateMessage> messageClass() {
        return LobbyUpdateMessage.class;
    }

    @Override
    public void encode(LobbyUpdateMessage msg, PacketBuffer buffer) {
        buffer.writeCompoundTag(msg.lobby.write(new CompoundNBT()));
        buffer.writeString(msg.bongoMessageType.name());
    }

    @Override
    public LobbyUpdateMessage decode(PacketBuffer buffer) {
        Lobby lobby = new Lobby();
        lobby.read(Objects.requireNonNull(buffer.readCompoundTag()));
        return new LobbyUpdateMessage(lobby, BongoMessageType.valueOf(buffer.readString(32767)));
    }

    public static class LobbyUpdateMessage {

        public final Lobby lobby;
        public final BongoMessageType bongoMessageType;

        public LobbyUpdateMessage(Lobby lobby) {
            this(lobby, BongoMessageType.GENERIC);
        }

        public LobbyUpdateMessage(Lobby lobby, BongoMessageType bongoMessageType) {
            this.lobby = lobby;
            this.bongoMessageType = bongoMessageType;
        }
    }
}
