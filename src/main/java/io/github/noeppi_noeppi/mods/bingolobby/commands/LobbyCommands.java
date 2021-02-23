package io.github.noeppi_noeppi.mods.bingolobby.commands;

import net.minecraft.item.DyeColor;
import net.minecraftforge.event.RegisterCommandsEvent;

import static com.mojang.brigadier.arguments.BoolArgumentType.bool;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static io.github.noeppi_noeppi.libx.command.UppercaseEnumArgument.enumArgument;
import static net.minecraft.command.Commands.argument;
import static net.minecraft.command.Commands.literal;
import static net.minecraft.command.arguments.EntityArgument.players;

public class LobbyCommands {

    public static void register(RegisterCommandsEvent event) {
        event.getDispatcher().register(literal("bingo").then(
                 literal("vip").requires(cs -> cs.hasPermissionLevel(2)).then(argument("players", players()).executes(new VipCommand()))
        ).then(
                literal("unvip").requires(cs -> cs.hasPermissionLevel(2)).then(argument("players", players()).executes(new UnvipCommand()))
        ).then(
                literal("vipteam").requires(cs -> cs.hasPermissionLevel(2)).then(argument("team", enumArgument(DyeColor.class)).executes(new VipTeamCommand()))
        ).then(
                literal("unvipteam").requires(cs -> cs.hasPermissionLevel(2)).then(argument("team", enumArgument(DyeColor.class)).executes(new UnvipTeamCommand()))
        ).then(
                literal("maxplayers").requires(cs -> cs.hasPermissionLevel(2)).then(argument("maxPlayers", integer(-1)).executes(new MaxPlayersCommand()))
        ).then(
                literal("countdown").requires(cs -> cs.hasPermissionLevel(2)).then(argument("countdown", integer(-1)).executes(new CountdownCommand()).then(argument("randomize_positions", bool()).executes(new CountdownCommand())))
        ));
    }
}
