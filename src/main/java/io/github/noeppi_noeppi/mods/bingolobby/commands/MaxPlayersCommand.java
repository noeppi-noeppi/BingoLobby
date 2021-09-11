package io.github.noeppi_noeppi.mods.bingolobby.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.noeppi_noeppi.mods.bingolobby.Lobby;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Player;

public class MaxPlayersCommand implements Command<CommandSourceStack> {

    @Override
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Player player = context.getSource().getPlayerOrException();
        Lobby lobby = Lobby.get(player.level);
        int maxPlayers = context.getArgument("maxPlayers", Integer.class);
        if (maxPlayers < 0) {
            maxPlayers = -1;
        }
        lobby.setMaxPlayers(maxPlayers);
        if (maxPlayers < 0) {
            context.getSource().sendSuccess(new TranslatableComponent("bingolobby.command.maxplayers.unlimited"), true);
        } else {
            context.getSource().sendSuccess(new TranslatableComponent("bingolobby.command.maxplayers.limited", Integer.toString(maxPlayers)), true);
        }
        return 0;
    }
}
