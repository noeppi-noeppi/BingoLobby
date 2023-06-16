package io.github.noeppi_noeppi.mods.bingolobby;

import io.github.noeppi_noeppi.mods.bongo.Bongo;
import io.github.noeppi_noeppi.mods.bongo.data.Team;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WoolCarpetBlock;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.file.Path;

public class EventListener {

    @SubscribeEvent
    public void playerTick(TickEvent.PlayerTickEvent event) {
        if (!event.player.level().isClientSide && event.player.tickCount % 2 == 1 && event.player.level().dimension().equals(ModDimensions.LOBBY_DIMENSION) && event.player instanceof ServerPlayer) {
            Bongo bongo = Bongo.get(event.player.level());
            DyeColor color = null;
            if (!event.player.isSpectator()) {
                Block block = event.player.level().getBlockState(event.player.blockPosition()).getBlock();
                color = block instanceof WoolCarpetBlock ? ((WoolCarpetBlock) block).getColor() : null;
                if (color == null) {
                    block = event.player.level().getBlockState(event.player.blockPosition().below()).getBlock();
                    color = block instanceof WoolCarpetBlock ? ((WoolCarpetBlock) block).getColor() : null;
                }
            }
            if (bongo.active() && !bongo.running() && !bongo.won()) {
                Team team = color == null ? null : bongo.getTeam(color);
                if (team == null || !team.hasPlayer(event.player)) {
                    Lobby lobby = Lobby.get(event.player.level());
                    MutableComponent tc = lobby.canAccess(event.player, team);
                    if (tc != null) {
                        event.player.sendSystemMessage(tc.withStyle(ChatFormatting.AQUA));
                        ModDimensions.teleportToLobby((ServerPlayer) event.player, false);
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
                event.player.sendSystemMessage(Component.translatable("bingolobby.nojoin.noactive").withStyle(ChatFormatting.AQUA));
                ModDimensions.teleportToLobby((ServerPlayer) event.player, false);
            }
        }
    }

    @SubscribeEvent
    public void lobbyTick(TickEvent.LevelTickEvent event) {
        if (event.phase == TickEvent.Phase.START && event.level instanceof ServerLevel level) {
            if (level.getServer().getTickCount() % 20 == 0 && ModDimensions.LOBBY_DIMENSION.equals(level.dimension())) {
                Lobby lobby = Lobby.get(event.level);
                lobby.tickCountdown();
            }
        }
    }

    @SubscribeEvent
    public void preServerStart(ServerAboutToStartEvent event) {
        Path dimensionFolder = FMLPaths.GAMEDIR.get().resolve(event.getServer().storageSource.getDimensionPath(ModDimensions.LOBBY_DIMENSION).normalize()).normalize();
        WorldPresetManager.copyWorld(dimensionFolder);
    }
}
