package io.github.noeppi_noeppi.mods.bingolobby;

import io.github.noeppi_noeppi.mods.bongo.Bongo;
import io.github.noeppi_noeppi.mods.bongo.network.BongoMessageType;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class DestinationControlEvents {
    
    @SubscribeEvent
    public void playerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getPlayer() instanceof ServerPlayerEntity) {
            World world = event.getPlayer().getEntityWorld();
            Bongo bongo = Bongo.get(world);
            if ((!bongo.running() && !bongo.won()) || bongo.getTeams().stream().noneMatch(t -> t.hasPlayer(event.getPlayer()))) {
                ModDimensions.teleportToLobby((ServerPlayerEntity) event.getPlayer(), true);
            }
            BingoLobby.getNetwork().updateLobby(event.getPlayer(), BongoMessageType.FORCE);
        }
    }
    
    @SubscribeEvent
    public void playerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getPlayer() instanceof ServerPlayerEntity) {
            World world = event.getPlayer().getEntityWorld();
            Bongo bongo = Bongo.get(world);
            if ((!bongo.running() && !bongo.won()) || bongo.getTeams().stream().noneMatch(t -> t.hasPlayer(event.getPlayer()))) {
                ModDimensions.teleportToLobby((ServerPlayerEntity) event.getPlayer(), false);
            }
        }
    }

    @SubscribeEvent
    public void playerChangeDim(PlayerEvent.PlayerChangedDimensionEvent event) {
        BingoLobby.getNetwork().updateLobby(event.getPlayer());
    }
}
