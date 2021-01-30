package io.github.noeppi_noeppi.mods.bingolobby.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.noeppi_noeppi.mods.bingolobby.Lobby;
import net.minecraft.command.CommandSource;
import net.minecraft.command.arguments.EntitySelector;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.List;

public class VipCommand implements Command<CommandSource> {

    @Override
    public int run(CommandContext<CommandSource> context) throws CommandSyntaxException {
        PlayerEntity player = context.getSource().asPlayer();
        Lobby lobby = Lobby.get(player.world);
        EntitySelector vipSelector = context.getArgument("players", EntitySelector.class);
        List<ServerPlayerEntity> players = vipSelector.selectPlayers(context.getSource());
        players.forEach(p -> lobby.setVip(p, true));
        context.getSource().sendFeedback(new TranslationTextComponent("bingolobby.command.vipadd", Integer.toString(players.size())), true);
        return 0;
    }
}
