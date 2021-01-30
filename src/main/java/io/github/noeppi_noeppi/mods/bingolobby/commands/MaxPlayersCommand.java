package io.github.noeppi_noeppi.mods.bingolobby.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.noeppi_noeppi.mods.bingolobby.Lobby;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.TranslationTextComponent;

public class MaxPlayersCommand implements Command<CommandSource> {

    @Override
    public int run(CommandContext<CommandSource> context) throws CommandSyntaxException {
        PlayerEntity player = context.getSource().asPlayer();
        Lobby lobby = Lobby.get(player.world);
        int maxPlayers = context.getArgument("maxPlayers", Integer.class);
        if (maxPlayers < 0) {
            maxPlayers = -1;
        }
        lobby.setMaxPlayers(maxPlayers);
        if (maxPlayers < 0) {
            context.getSource().sendFeedback(new TranslationTextComponent("bingolobby.command.maxplayers.unlimited"), true);
        } else {
            context.getSource().sendFeedback(new TranslationTextComponent("bingolobby.command.maxplayers.limited", Integer.toString(maxPlayers)), true);
        }
        return 0;
    }
}
