package io.github.noeppi_noeppi.mods.bingolobby.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import org.moddingx.libx.render.ClientTickHandler;
import org.moddingx.libx.render.RenderHelper;
import io.github.noeppi_noeppi.mods.bingolobby.Lobby;
import io.github.noeppi_noeppi.mods.bingolobby.ModDimensions;
import io.github.noeppi_noeppi.mods.bingolobby.config.LobbyConfig;
import io.github.noeppi_noeppi.mods.bongo.Bongo;
import io.github.noeppi_noeppi.mods.bongo.Keybinds;
import io.github.noeppi_noeppi.mods.bongo.data.Team;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.*;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class RenderOverlay {

    private static final double relativeHeightMax = 0.3;
    private static final double relativeWidthMax = 0.3;
    private static final int padding = 4;
    
    @SubscribeEvent
    public void renderOverlay(RenderGameOverlayEvent.Post event) {
        PoseStack poseStack = event.getPoseStack();
        MultiBufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
        Minecraft mc = Minecraft.getInstance();
        if (mc.level != null && mc.player != null && ModDimensions.LOBBY_DIMENSION.equals(mc.player.level.dimension()) && (mc.screen == null || mc.screen instanceof ChatScreen) && !Keybinds.BIG_OVERLAY.isDown() && event.getType() == RenderGameOverlayEvent.ElementType.ALL) {

            Bongo bongo = Bongo.get(mc.level);
            Lobby lobby = Lobby.get(mc.level);
            ClientPacketListener netHandler = mc.player.connection;

            MutableComponent countdown = null;
            if (lobby.getCountdown() >= 0) {
                countdown = Component.translatable("bingolobby.scoreboard.countdown",
                        Component.literal(Integer.toString(lobby.getCountdown() / 60)).withStyle(Style.EMPTY.applyFormats(ChatFormatting.GOLD, ChatFormatting.BOLD)),
                        Component.literal(String.format("%02d", lobby.getCountdown() % 60)).withStyle(Style.EMPTY.applyFormats(ChatFormatting.GOLD, ChatFormatting.BOLD))
                ).withStyle(ChatFormatting.WHITE);
            }

            MutableComponent playersOnline = Component.translatable("bingolobby.scoreboard.online",
                    Component.literal(Integer.toString(netHandler.getOnlinePlayers().size())).withStyle(Style.EMPTY.applyFormats(ChatFormatting.GOLD, ChatFormatting.BOLD))
            ).withStyle(ChatFormatting.WHITE);

            MutableComponent perTeam;
            if (lobby.getMaxPlayers() == 0) {
                perTeam = Component.translatable("bingolobby.scoreboard.no_joining").withStyle(ChatFormatting.WHITE);
            } else if (lobby.getMaxPlayers() < 0) {
                perTeam = Component.translatable("bingolobby.scoreboard.players_per_team",
                        Component.literal("∞").withStyle(Style.EMPTY.applyFormats(ChatFormatting.GOLD))
                ).withStyle(ChatFormatting.WHITE);
            } else {
                perTeam = Component.translatable("bingolobby.scoreboard.players_per_team",
                        Component.literal(Integer.toString(lobby.getMaxPlayers())).withStyle(Style.EMPTY.applyFormats(ChatFormatting.GOLD, ChatFormatting.BOLD))
                ).withStyle(ChatFormatting.WHITE);
            }

            MutableComponent statusValue = Component.translatable(lobby.vip(mc.player) ? "bingolobby.scoreboard.status.vip" : "bingolobby.scoreboard.status.player").withStyle(Style.EMPTY.applyFormats(lobby.vip(mc.player) ? ChatFormatting.LIGHT_PURPLE : ChatFormatting.GOLD, ChatFormatting.BOLD));
            MutableComponent status = Component.translatable("bingolobby.scoreboard.status.info", statusValue).withStyle(ChatFormatting.WHITE);

            MutableComponent teamText;
            if (bongo.active()) {
                Team team = bongo.active() ? bongo.getTeam(mc.player) : null;
                teamText = team == null ? Component.translatable("bingolobby.scoreboard.noteam") : team.getName().withStyle(ChatFormatting.BOLD);
            } else {
                teamText = Component.translatable("bingolobby.scoreboard.nogame");
            }
            
            Font font = mc.font;
            int height = 1 + (5 * font.lineHeight) + (11 * padding);
            if (!LobbyConfig.subtitle.isEmpty()) height += (font.lineHeight + padding);
            if (!LobbyConfig.subtitle2.isEmpty()) height += (font.lineHeight + padding);
            int width = 100;
            width = Math.max(width, font.width(LobbyConfig.title));
            if (countdown != null) {
                width = Math.max(width, font.width(countdown));
            }
            for (Component subtitle : LobbyConfig.subtitle) {
                width = Math.max(width, font.width(subtitle));
            }
            for (Component subtitle2 : LobbyConfig.subtitle2) {
                width = Math.max(width, font.width(subtitle2));
            }
            width = Math.max(width, font.width(playersOnline));
            width = Math.max(width, font.width(perTeam));
            width = Math.max(width, font.width(status));
            width = Math.max(width, font.width(teamText));
            width = Math.max(width, font.width(LobbyConfig.title));
            width += (2 * padding);

            double scaleFactor = Math.min(((mc.getWindow().getGuiScaledWidth() * relativeWidthMax) / width), ((mc.getWindow().getGuiScaledHeight() * relativeHeightMax) / height));
            double totalWidth = width * scaleFactor;
            double totalHeight = height * scaleFactor;
            
            poseStack.pushPose();
            poseStack.translate(mc.getWindow().getGuiScaledWidth() - totalWidth, (mc.getWindow().getGuiScaledHeight() / 2d) - (totalHeight / 2), 0);
            poseStack.scale((float) scaleFactor, (float) scaleFactor, 1);
            
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.setShaderTexture(0, RenderHelper.TEXTURE_WHITE);
            RenderSystem.setShaderColor(0, 0, 0, (float) (double) mc.options.textBackgroundOpacity().get());
            GuiComponent.blit(poseStack, 0, 0, 0, 0, width, height, 256, 256);
            RenderSystem.disableBlend();

            poseStack.translate(padding, padding, 100);
            
            this.renderLine(poseStack, font, LobbyConfig.title, 0, width);

            RenderSystem.setShaderTexture(0, RenderHelper.TEXTURE_WHITE);
            RenderHelper.rgb(0x00FF5B);
            GuiComponent.blit(poseStack, 0, padding + font.lineHeight + (padding / 2), 0, 0, Math.round(width - (2 * padding)), 1, 256, 256);

            if (!LobbyConfig.subtitle.isEmpty()) {
                if (countdown != null && !LobbyConfig.countdown_in_subtitle2) {
                    this.renderLine(poseStack, font, countdown, (float) ((font.lineHeight + padding) + (2 * padding)), width);
                } else {
                    int subtitleIdx = (ClientTickHandler.ticksInGame / 60) % LobbyConfig.subtitle.size();
                    this.renderLine(poseStack, font, LobbyConfig.subtitle.get(subtitleIdx), (float) ((font.lineHeight + padding) + (2 * padding)), width);
                }
            }
            
            if (!LobbyConfig.subtitle2.isEmpty()) {
                if (countdown != null && LobbyConfig.countdown_in_subtitle2) {
                    this.renderLine(poseStack, font, countdown, (float) ((font.lineHeight + padding) + (2 * padding)), width);
                } else {
                    int subtitleIdx = (ClientTickHandler.ticksInGame / 60) % LobbyConfig.subtitle2.size();
                    this.renderLine(poseStack, font, LobbyConfig.subtitle2.get(subtitleIdx), (float) (((LobbyConfig.subtitle.isEmpty() ? 1 : 2) * (font.lineHeight + padding)) + (2 * padding)), width);
                }
            }
            
            int lines = 1;
            if (!LobbyConfig.subtitle.isEmpty()) lines += 1;
            if (!LobbyConfig.subtitle2.isEmpty()) lines += 1;
            poseStack.translate(0, (lines * (font.lineHeight + padding)) + (3 * padding) + 1, 0);
            
            RenderSystem.setShaderTexture(0, RenderHelper.TEXTURE_WHITE);
            RenderHelper.rgb(0x00FF5B);
            GuiComponent.blit(poseStack, 0, -(padding / 2), 0, 0, Math.round(width - (2 * padding)), 1, 256, 256);
            
            this.renderLine(poseStack, font, playersOnline, padding, width);
            this.renderLine(poseStack, font, perTeam, (2 * padding) + font.lineHeight, width);
            
            this.renderLine(poseStack, font, status, (4 * padding) + (2 * font.lineHeight), width);
            this.renderLine(poseStack, font, teamText, (5 * padding) + (3 * font.lineHeight), width);
            
            RenderHelper.resetColor();
            poseStack.popPose();
        }
    }

    private void renderLine(PoseStack poseStack, Font font, Component line, double y, int width) {
        double x = Math.max(0, ((width - (2 * padding)) / 2d) - (font.width(line) / 2d));
        font.drawShadow(poseStack, line, (float) x, (float) y, 0xFFFFFFFF);
    }
}
