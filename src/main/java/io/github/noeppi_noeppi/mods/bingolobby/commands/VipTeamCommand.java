package io.github.noeppi_noeppi.mods.bingolobby.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.noeppi_noeppi.mods.bingolobby.Lobby;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.util.text.TranslationTextComponent;

public class VipTeamCommand implements Command<CommandSource> {

    @Override
    public int run(CommandContext<CommandSource> context) throws CommandSyntaxException {
        PlayerEntity player = context.getSource().asPlayer();
        Lobby lobby = Lobby.get(player.world);
        DyeColor team = context.getArgument("team", DyeColor.class);
        lobby.setVipTeam(team, true);
        context.getSource().sendFeedback(new TranslationTextComponent("bingolobby.command.vipteamadd", new TranslationTextComponent(team.getTranslationKey())), true);
        return 0;
    }
}
