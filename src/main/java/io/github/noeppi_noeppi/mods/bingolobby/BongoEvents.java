package io.github.noeppi_noeppi.mods.bingolobby;

import io.github.noeppi_noeppi.mods.bongo.event.BongoChangeManyTeamsEvent;
import io.github.noeppi_noeppi.mods.bongo.event.BongoChangeTeamEvent;
import io.github.noeppi_noeppi.mods.bongo.event.BongoPickLevelEvent;
import io.github.noeppi_noeppi.mods.bongo.event.BongoStopEvent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class BongoEvents {

    @SubscribeEvent
    public void changeTeam(BongoChangeTeamEvent event) {
        event.setCanceled(true);
        event.setFailureMessage(new TranslatableComponent("bingolobby.command.disabled"));
    }

    @SubscribeEvent
    public void changeMultipleTeams(BongoChangeManyTeamsEvent event) {
        event.setCanceled(true);
        event.setFailureMessage(new TranslatableComponent("bingolobby.command.disabled"));
    }
    
    @SubscribeEvent
    public void bongoStop(BongoStopEvent.Player event) {
        ModDimensions.teleportToLobby(event.getPlayer(), true);
    }
    
    @SubscribeEvent
    public void pickBongoWorld(BongoPickLevelEvent event) {
        if (event.getLevel().dimension().equals(ModDimensions.LOBBY_DIMENSION)) {
            event.setLevel(event.getServer().getLevel(Level.OVERWORLD));
        }
    }
}
