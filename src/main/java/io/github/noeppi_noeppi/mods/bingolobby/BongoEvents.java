package io.github.noeppi_noeppi.mods.bingolobby;

import io.github.noeppi_noeppi.mods.bongo.command.event.BongoChangeManyTeamsEvent;
import io.github.noeppi_noeppi.mods.bongo.command.event.BongoChangeTeamEvent;
import io.github.noeppi_noeppi.mods.bongo.command.event.BongoPickWorldEvent;
import io.github.noeppi_noeppi.mods.bongo.command.event.BongoStopEvent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class BongoEvents {

    @SubscribeEvent
    public void changeTeam(BongoChangeTeamEvent event) {
        event.setCanceled(true);
        event.setFailureMessage(new TranslationTextComponent("bingolobby.command.disabled"));
    }

    @SubscribeEvent
    public void changeMultipleTeams(BongoChangeManyTeamsEvent event) {
        event.setCanceled(true);
        event.setFailureMessage(new TranslationTextComponent("bingolobby.command.disabled"));
    }
    
    @SubscribeEvent
    public void bongoStop(BongoStopEvent.Player event) {
        ModDimensions.teleportToLobby(event.getPlayer(), true);
    }
    
    @SubscribeEvent
    public void pickBongoWorld(BongoPickWorldEvent event) {
        if (event.getWorld().getDimensionKey().equals(ModDimensions.LOBBY_DIMENSION)) {
            event.setWorld(event.getServer().getWorld(World.OVERWORLD));
        }
    }
}
