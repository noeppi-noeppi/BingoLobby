package io.github.noeppi_noeppi.mods.bingolobby.network;

import org.moddingx.libx.network.PacketSerializer;
import io.github.noeppi_noeppi.mods.bingolobby.Lobby;
import io.github.noeppi_noeppi.mods.bongo.network.BongoMessageType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

import java.util.Objects;

public class LobbyUpdateSerializer implements PacketSerializer<LobbyUpdateSerializer.LobbyUpdateMessage> {

    @Override
    public Class<LobbyUpdateMessage> messageClass() {
        return LobbyUpdateMessage.class;
    }

    @Override
    public void encode(LobbyUpdateMessage msg, FriendlyByteBuf buffer) {
        buffer.writeNbt(msg.lobby.save(new CompoundTag()));
        buffer.writeUtf(msg.bongoMessageType.name());
    }

    @Override
    public LobbyUpdateMessage decode(FriendlyByteBuf buffer) {
        Lobby lobby = new Lobby();
        lobby.load(Objects.requireNonNull(buffer.readNbt()));
        return new LobbyUpdateMessage(lobby, BongoMessageType.valueOf(buffer.readUtf(32767)));
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
