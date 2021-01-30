package io.github.noeppi_noeppi.mods.bingolobby.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import io.github.noeppi_noeppi.libx.command.UppercaseEnumArgument;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.item.DyeColor;
import net.minecraftforge.event.RegisterCommandsEvent;

public class LobbyCommands {

    public static void register(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("bingo").then(
                 Commands.literal("vip").requires(cs -> cs.hasPermissionLevel(2)).then(Commands.argument("players", EntityArgument.players()).executes(new VipCommand()))
        ).then(
                Commands.literal("unvip").requires(cs -> cs.hasPermissionLevel(2)).then(Commands.argument("players", EntityArgument.players()).executes(new UnvipCommand()))
        ).then(
                Commands.literal("vipteam").requires(cs -> cs.hasPermissionLevel(2)).then(Commands.argument("team", UppercaseEnumArgument.enumArgument(DyeColor.class)).executes(new VipTeamCommand()))
        ).then(
                Commands.literal("unvipteam").requires(cs -> cs.hasPermissionLevel(2)).then(Commands.argument("team", UppercaseEnumArgument.enumArgument(DyeColor.class)).executes(new UnvipTeamCommand()))
        ).then(
                Commands.literal("maxplayers").requires(cs -> cs.hasPermissionLevel(2)).then(Commands.argument("maxPlayers", IntegerArgumentType.integer(-1)).executes(new MaxPlayersCommand()))
        ));
    }
}
