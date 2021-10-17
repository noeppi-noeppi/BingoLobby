package io.github.noeppi_noeppi.mods.bingolobby;

import io.github.noeppi_noeppi.mods.bongo.Bongo;
import io.github.noeppi_noeppi.mods.bongo.data.Team;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WoolCarpetBlock;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fmlserverevents.FMLServerAboutToStartEvent;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipFile;

public class EventListener {

    @SubscribeEvent
    public void playerTick(TickEvent.PlayerTickEvent event) {
        if (!event.player.level.isClientSide && event.player.tickCount % 2 == 1 && event.player.level.dimension().equals(ModDimensions.LOBBY_DIMENSION) && event.player instanceof ServerPlayer) {
            Bongo bongo = Bongo.get(event.player.level);
            DyeColor color = null;
            if (!event.player.isSpectator()) {
                Block block = event.player.level.getBlockState(event.player.blockPosition()).getBlock();
                color = block instanceof WoolCarpetBlock ? ((WoolCarpetBlock) block).getColor() : null;
                if (color == null) {
                    block = event.player.level.getBlockState(event.player.blockPosition().below()).getBlock();
                    color = block instanceof WoolCarpetBlock ? ((WoolCarpetBlock) block).getColor() : null;
                }
            }
            if (bongo.active() && !bongo.running() && !bongo.won()) {
                Team team = color == null ? null : bongo.getTeam(color);
                if (team == null || !team.hasPlayer(event.player)) {
                    Lobby lobby = Lobby.get(event.player.level);
                    MutableComponent tc = lobby.canAccess(event.player, team);
                    if (tc != null) {
                        event.player.sendMessage(tc.withStyle(ChatFormatting.AQUA), event.player.getUUID());
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
                event.player.sendMessage(new TranslatableComponent("bingolobby.nojoin.noactive").withStyle(ChatFormatting.AQUA), event.player.getUUID());
                ModDimensions.teleportToLobby((ServerPlayer) event.player, false);
            }
        }
    }

    @SubscribeEvent
    public void lobbyTick(TickEvent.WorldTickEvent event) {
        if (event.phase == TickEvent.Phase.START && event.world instanceof ServerLevel level) {
            if (level.getServer().getTickCount() % 20 == 0 && ModDimensions.LOBBY_DIMENSION.equals(level.dimension())) {
                Lobby lobby = Lobby.get(event.world);
                lobby.tickCountdown();
            }
        }
    }

    @SubscribeEvent
    public void copyWorldPreset(FMLServerAboutToStartEvent event) throws IOException {
        Path dimensionFolder = FMLPaths.GAMEDIR.get().resolve(event.getServer().storageSource.getDimensionPath(ModDimensions.LOBBY_DIMENSION).toPath()).normalize();
        if (!Files.exists(dimensionFolder)) {
            Path zipPath = FMLPaths.CONFIGDIR.get().resolve("bingolobby-preset.zip");
            if (Files.exists(zipPath)) {
                ZipFile zipFile = new ZipFile(zipPath.toFile());
                zipFile.entries().asIterator().forEachRemaining(entry -> {
                    try {
                        File destFile = new File(dimensionFolder.toFile(), entry.getName());
                        if (entry.isDirectory()) {
                            //noinspection ResultOfMethodCallIgnored
                            destFile.mkdirs();
                        } else {
                            FileOutputStream outputStream = new FileOutputStream(destFile);
                            zipFile.getInputStream(entry).transferTo(outputStream);
                            outputStream.close();
                        }
                    } catch (IOException e) {
                        BingoLobby.getInstance().logger.warn("Failed to copy preset file.", e);
                    }
                });
            }
        }
    }
}
