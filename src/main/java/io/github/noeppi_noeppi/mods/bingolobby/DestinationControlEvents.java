package io.github.noeppi_noeppi.mods.bingolobby;

import io.github.noeppi_noeppi.mods.bongo.Bongo;
import io.github.noeppi_noeppi.mods.bongo.network.BongoMessageType;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class DestinationControlEvents {
    
    @SubscribeEvent
    public void playerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer) {
            Level level = event.getEntity().getCommandSenderWorld();
            Bongo bongo = Bongo.get(level);
            if ((!bongo.running() && !bongo.won()) || bongo.getTeams().stream().noneMatch(t -> t.hasPlayer(event.getEntity()))) {
                ModDimensions.teleportToLobby((ServerPlayer) event.getEntity(), true);
            }
            BingoLobby.getNetwork().updateLobby(event.getEntity(), BongoMessageType.FORCE);
        }
    }
    
    @SubscribeEvent
    public void playerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer) {
            Level level = event.getEntity().getCommandSenderWorld();
            Bongo bongo = Bongo.get(level);
            if ((!bongo.running() && !bongo.won()) || bongo.getTeams().stream().noneMatch(t -> t.hasPlayer(event.getEntity()))) {
                ModDimensions.teleportToLobby((ServerPlayer) event.getEntity(), false);
            }
        }
    }

    @SubscribeEvent
    public void playerChangeDim(PlayerEvent.PlayerChangedDimensionEvent event) {
        BingoLobby.getNetwork().updateLobby(event.getEntity());
    }
}
