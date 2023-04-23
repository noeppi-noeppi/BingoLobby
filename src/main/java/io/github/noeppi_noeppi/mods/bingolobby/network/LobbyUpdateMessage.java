package io.github.noeppi_noeppi.mods.bingolobby.network;

import io.github.noeppi_noeppi.mods.bingolobby.Lobby;
import io.github.noeppi_noeppi.mods.bongo.network.BongoMessageType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import org.moddingx.libx.network.PacketHandler;
import org.moddingx.libx.network.PacketSerializer;

import java.util.Objects;
import java.util.function.Supplier;

public record LobbyUpdateMessage(Lobby lobby, BongoMessageType type) {
    
    public LobbyUpdateMessage(Lobby lobby) {
        this(lobby, BongoMessageType.GENERIC);
    }
    
    public static class Serializer implements PacketSerializer<LobbyUpdateMessage> {

        @Override
        public Class<LobbyUpdateMessage> messageClass() {
            return LobbyUpdateMessage.class;
        }

        @Override
        public void encode(LobbyUpdateMessage msg, FriendlyByteBuf buffer) {
            buffer.writeNbt(msg.lobby().save(new CompoundTag()));
            buffer.writeUtf(msg.type().name());
        }

        @Override
        public LobbyUpdateMessage decode(FriendlyByteBuf buffer) {
            Lobby lobby = new Lobby();
            lobby.load(Objects.requireNonNull(buffer.readNbt()));
            return new LobbyUpdateMessage(lobby, BongoMessageType.valueOf(buffer.readUtf()));
        }
    }
    
    public static class Handler implements PacketHandler<LobbyUpdateMessage> {

        @Override
        public Target target() {
            return Target.MAIN_THREAD;
        }

        @Override
        public boolean handle(LobbyUpdateMessage msg, Supplier<NetworkEvent.Context> ctx) {
            Lobby.updateClient(msg.lobby(), msg.type());
            return true;
        }
    }
}
