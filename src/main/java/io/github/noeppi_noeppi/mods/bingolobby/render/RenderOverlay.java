package io.github.noeppi_noeppi.mods.bingolobby.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.noeppi_noeppi.libx.render.ClientTickHandler;
import io.github.noeppi_noeppi.libx.render.RenderHelper;
import io.github.noeppi_noeppi.mods.bingolobby.Lobby;
import io.github.noeppi_noeppi.mods.bingolobby.ModDimensions;
import io.github.noeppi_noeppi.mods.bingolobby.config.LobbyConfig;
import io.github.noeppi_noeppi.mods.bongo.Bongo;
import io.github.noeppi_noeppi.mods.bongo.Keybinds;
import io.github.noeppi_noeppi.mods.bongo.data.Team;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.util.text.*;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class RenderOverlay {

    private static final double relativeHeightMax = 0.3;
    private static final double relativeWidthMax = 0.3;
    private static final int padding = 4;
    
    @SubscribeEvent
    public void renderOverlay(RenderGameOverlayEvent.Post event) {
        MatrixStack matrixStack = event.getMatrixStack();
        IRenderTypeBuffer buffer = Minecraft.getInstance().getRenderTypeBuffers().getBufferSource();
        Minecraft mc = Minecraft.getInstance();
        if (mc.world != null && mc.player != null && mc.player.world != null && ModDimensions.LOBBY_DIMENSION.equals(mc.player.world.getDimensionKey()) && (mc.currentScreen == null || mc.currentScreen instanceof ChatScreen) && !Keybinds.BIG_OVERLAY.isKeyDown() && event.getType() == RenderGameOverlayEvent.ElementType.ALL) {

            Bongo bongo = Bongo.get(mc.world);
            Lobby lobby = Lobby.get(mc.world);
            ClientPlayNetHandler netHandler = mc.player.connection;

            IFormattableTextComponent countdown = null;
            if (lobby.getCountdown() >= 0) {
                countdown = new TranslationTextComponent("bingolobby.scoreboard.countdown",
                        new StringTextComponent(Integer.toString(lobby.getCountdown() / 60)).mergeStyle(Style.EMPTY.mergeWithFormatting(TextFormatting.GOLD, TextFormatting.BOLD)),
                        new StringTextComponent(String.format("%02d", lobby.getCountdown() % 60)).mergeStyle(Style.EMPTY.mergeWithFormatting(TextFormatting.GOLD, TextFormatting.BOLD))
                ).mergeStyle(TextFormatting.WHITE);
            }

            IFormattableTextComponent playersOnline = new TranslationTextComponent("bingolobby.scoreboard.online",
                    new StringTextComponent(Integer.toString(netHandler.getPlayerInfoMap().size())).mergeStyle(Style.EMPTY.mergeWithFormatting(TextFormatting.GOLD, TextFormatting.BOLD))
            ).mergeStyle(TextFormatting.WHITE);

            IFormattableTextComponent perTeam;
            if (lobby.getMaxPlayers() == 0) {
                perTeam = new TranslationTextComponent("bingolobby.scoreboard.no_joining").mergeStyle(TextFormatting.WHITE);
            } else if (lobby.getMaxPlayers() < 0) {
                perTeam = new TranslationTextComponent("bingolobby.scoreboard.players_per_team",
                        new StringTextComponent("∞").mergeStyle(Style.EMPTY.mergeWithFormatting(TextFormatting.GOLD))
                ).mergeStyle(TextFormatting.WHITE);
            } else {
                perTeam = new TranslationTextComponent("bingolobby.scoreboard.players_per_team",
                        new StringTextComponent(Integer.toString(lobby.getMaxPlayers())).mergeStyle(Style.EMPTY.mergeWithFormatting(TextFormatting.GOLD, TextFormatting.BOLD))
                ).mergeStyle(TextFormatting.WHITE);
            }

            IFormattableTextComponent statusValue = new TranslationTextComponent(lobby.vip(mc.player) ? "bingolobby.scoreboard.status.vip" : "bingolobby.scoreboard.status.player").mergeStyle(Style.EMPTY.mergeWithFormatting(lobby.vip(mc.player) ? TextFormatting.LIGHT_PURPLE : TextFormatting.GOLD, TextFormatting.BOLD));
            IFormattableTextComponent status = new TranslationTextComponent("bingolobby.scoreboard.status.info", statusValue).mergeStyle(TextFormatting.WHITE);

            IFormattableTextComponent teamText;
            if (bongo.active()) {
                Team team = bongo.active() ? bongo.getTeam(mc.player) : null;
                teamText = team == null ? new TranslationTextComponent("bingolobby.scoreboard.noteam") : team.getName().mergeStyle(TextFormatting.BOLD);
            } else {
                teamText = new TranslationTextComponent("bingolobby.scoreboard.nogame");
            }
            
            FontRenderer font = mc.fontRenderer;
            int height = 1 + (5 * font.FONT_HEIGHT) + (11 * padding);
            if (!LobbyConfig.subtitle.isEmpty()) height += (font.FONT_HEIGHT + padding);
            if (!LobbyConfig.subtitle2.isEmpty()) height += (font.FONT_HEIGHT + padding);
            int width = 100;
            width = Math.max(width, font.getStringPropertyWidth(LobbyConfig.title));
            if (countdown != null) {
                width = Math.max(width, font.getStringPropertyWidth(countdown));
            }
            for (IFormattableTextComponent subtitle : LobbyConfig.subtitle) {
                width = Math.max(width, font.getStringPropertyWidth(subtitle));
            }
            for (IFormattableTextComponent subtitle2 : LobbyConfig.subtitle2) {
                width = Math.max(width, font.getStringPropertyWidth(subtitle2));
            }
            width = Math.max(width, font.getStringPropertyWidth(playersOnline));
            width = Math.max(width, font.getStringPropertyWidth(perTeam));
            width = Math.max(width, font.getStringPropertyWidth(status));
            width = Math.max(width, font.getStringPropertyWidth(teamText));
            width = Math.max(width, font.getStringPropertyWidth(LobbyConfig.title));
            width += (2 * padding);

            double scaleFactor = Math.min(((mc.getMainWindow().getScaledWidth() * relativeWidthMax) / width), ((mc.getMainWindow().getScaledHeight() * relativeHeightMax) / height));
            double totalWidth = width * scaleFactor;
            double totalHeight = height * scaleFactor;
            
            matrixStack.push();
            matrixStack.translate(mc.getMainWindow().getScaledWidth() - totalWidth, (mc.getMainWindow().getScaledHeight() / 2d) - (totalHeight / 2), 0);
            matrixStack.scale((float) scaleFactor, (float) scaleFactor, 1);
            
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            Minecraft.getInstance().getTextureManager().bindTexture(RenderHelper.TEXTURE_WHITE);
            //noinspection deprecation
            RenderSystem.color4f(0, 0, 0, (float) mc.gameSettings.accessibilityTextBackgroundOpacity);
            AbstractGui.blit(matrixStack, 0, 0, 0, 0, width, height, 256, 256);
            RenderSystem.disableBlend();

            matrixStack.translate(padding, padding, 100);
            
            this.renderLine(matrixStack, font, LobbyConfig.title, 0, width);

            Minecraft.getInstance().getTextureManager().bindTexture(RenderHelper.TEXTURE_WHITE);
            RenderHelper.color(0x00FF5B);
            AbstractGui.blit(matrixStack, 0, padding + font.FONT_HEIGHT + (padding / 2), 0, 0, Math.round(width - (2 * padding)), 1, 256, 256);

            if (!LobbyConfig.subtitle.isEmpty()) {
                if (countdown != null && !LobbyConfig.countdown_in_subtitle2) {
                    this.renderLine(matrixStack, font, countdown, (float) ((font.FONT_HEIGHT + padding) + (2 * padding)), width);
                } else {
                    int subtitleIdx = (ClientTickHandler.ticksInGame / 60) % LobbyConfig.subtitle.size();
                    this.renderLine(matrixStack, font, LobbyConfig.subtitle.get(subtitleIdx), (float) ((font.FONT_HEIGHT + padding) + (2 * padding)), width);
                }
            }
            
            if (!LobbyConfig.subtitle2.isEmpty()) {
                if (countdown != null && LobbyConfig.countdown_in_subtitle2) {
                    this.renderLine(matrixStack, font, countdown, (float) ((font.FONT_HEIGHT + padding) + (2 * padding)), width);
                } else {
                    int subtitleIdx = (ClientTickHandler.ticksInGame / 60) % LobbyConfig.subtitle2.size();
                    this.renderLine(matrixStack, font, LobbyConfig.subtitle2.get(subtitleIdx), (float) (((LobbyConfig.subtitle.isEmpty() ? 1 : 2) * (font.FONT_HEIGHT + padding)) + (2 * padding)), width);
                }
            }
            
            int lines = 1;
            if (!LobbyConfig.subtitle.isEmpty()) lines += 1;
            if (!LobbyConfig.subtitle2.isEmpty()) lines += 1;
            matrixStack.translate(0, (lines * (font.FONT_HEIGHT + padding)) + (3 * padding) + 1, 0);
            
            Minecraft.getInstance().getTextureManager().bindTexture(RenderHelper.TEXTURE_WHITE);
            RenderHelper.color(0x00FF5B);
            AbstractGui.blit(matrixStack, 0, -(padding / 2), 0, 0, Math.round(width - (2 * padding)), 1, 256, 256);
            
            this.renderLine(matrixStack, font, playersOnline, padding, width);
            this.renderLine(matrixStack, font, perTeam, (2 * padding) + font.FONT_HEIGHT, width);
            
            this.renderLine(matrixStack, font, status, (4 * padding) + (2 * font.FONT_HEIGHT), width);
            this.renderLine(matrixStack, font, teamText, (5 * padding) + (3 * font.FONT_HEIGHT), width);
            
            RenderHelper.resetColor();
            matrixStack.pop();
        }
    }

    private void renderLine(MatrixStack matrixStack, FontRenderer font, ITextComponent line, double y, int width) {
        double x = Math.max(0, ((width - (2 * padding)) / 2d) - (font.getStringPropertyWidth(line) / 2d));
        font.drawTextWithShadow(matrixStack, line, (float) x, (float) y, 0xFFFFFFFF);
    }
}
