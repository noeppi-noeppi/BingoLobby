package io.github.noeppi_noeppi.mods.bingolobby.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.noeppi_noeppi.mods.bingolobby.Lobby;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public class VipCommand implements Command<CommandSourceStack> {

    @Override
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Player player = context.getSource().getPlayerOrException();
        Lobby lobby = Lobby.get(player.level());
        EntitySelector vipSelector = context.getArgument("players", EntitySelector.class);
        List<ServerPlayer> players = vipSelector.findPlayers(context.getSource());
        players.forEach(p -> lobby.setVip(p, true));
        context.getSource().sendSuccess(() -> Component.translatable("bingolobby.command.vipadd", Integer.toString(players.size())), true);
        return 0;
    }
}
