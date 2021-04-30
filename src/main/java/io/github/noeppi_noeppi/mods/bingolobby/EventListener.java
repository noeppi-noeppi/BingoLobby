package io.github.noeppi_noeppi.mods.bingolobby;

import io.github.noeppi_noeppi.mods.bongo.Bongo;
import io.github.noeppi_noeppi.mods.bongo.data.Team;
import net.minecraft.block.Block;
import net.minecraft.block.CarpetBlock;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class EventListener {

    @SubscribeEvent
    public void playerTick(TickEvent.PlayerTickEvent event) {
        if (!event.player.world.isRemote && event.player.ticksExisted % 2 == 1 && event.player.world.getDimensionKey().equals(ModDimensions.LOBBY_DIMENSION) && event.player instanceof ServerPlayerEntity) {
            Bongo bongo = Bongo.get(event.player.world);
            DyeColor color = null;
            if (!event.player.isSpectator()) {
                Block block = event.player.world.getBlockState(event.player.getPosition()).getBlock();
                color = block instanceof CarpetBlock ? ((CarpetBlock) block).getColor() : null;
                if (color == null) {
                    block = event.player.world.getBlockState(event.player.getPosition().down()).getBlock();
                    color = block instanceof CarpetBlock ? ((CarpetBlock) block).getColor() : null;
                }
            }
            if (bongo.active() && !bongo.running() && !bongo.won()) {
                Team team = color == null ? null : bongo.getTeam(color);
                if (team == null || !team.hasPlayer(event.player)) {
                    Lobby lobby = Lobby.get(event.player.world);
                    IFormattableTextComponent tc = lobby.canAccess(event.player, team);
                    if (tc != null) {
                        event.player.sendMessage(tc.mergeStyle(TextFormatting.AQUA), event.player.getUniqueID());
                        ModDimensions.teleportToLobby((ServerPlayerEntity) event.player, false);
                    } else {
                        if (team == null) {
                            Team currentTeam = bongo.getTeam(event.player);
                            if (currentTeam != null) {
                                currentTeam.removePlayer(event.player);
                            }
                        } else if (!team.hasPlayer(event.player)) {
                            team.addPlayer(event.player);
                        }
                    }
                }
            } else if (color != null) {
                event.player.sendMessage(new TranslationTextComponent("bingolobby.nojoin.noactive").mergeStyle(TextFormatting.AQUA), event.player.getUniqueID());
                ModDimensions.teleportToLobby((ServerPlayerEntity) event.player, false);
            }
        }
    }
    
    @SubscribeEvent
    public void lobbyTick(TickEvent.WorldTickEvent event) {
        if (event.phase == TickEvent.Phase.START && event.world instanceof ServerWorld) {
            ServerWorld world = (ServerWorld) event.world;
            if (world.getServer().getTickCounter() % 20 == 0 && ModDimensions.LOBBY_DIMENSION.equals(world.getDimensionKey())) {
                Lobby lobby = Lobby.get(event.world);
                lobby.tickCountdown();
            }
        }
    }
}
