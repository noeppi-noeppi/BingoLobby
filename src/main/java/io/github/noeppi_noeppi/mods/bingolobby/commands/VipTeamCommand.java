package io.github.noeppi_noeppi.mods.bingolobby.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.noeppi_noeppi.mods.bingolobby.Lobby;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;

public class VipTeamCommand implements Command<CommandSourceStack> {

    @Override
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Player player = context.getSource().getPlayerOrException();
        Lobby lobby = Lobby.get(player.level);
        DyeColor team = context.getArgument("team", DyeColor.class);
        lobby.setVipTeam(team, true);
        context.getSource().sendSuccess(Component.translatable("bingolobby.command.vipteamadd", Component.translatable(team.getName())), true);
        return 0;
    }
}
