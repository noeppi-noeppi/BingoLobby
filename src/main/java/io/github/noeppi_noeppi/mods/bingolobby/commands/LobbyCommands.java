package io.github.noeppi_noeppi.mods.bingolobby.commands;

import net.minecraft.world.item.DyeColor;
import net.minecraftforge.event.RegisterCommandsEvent;

import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;
import static net.minecraft.commands.arguments.EntityArgument.players;
import static org.moddingx.libx.command.EnumArgument2.enumArgument;

public class LobbyCommands {

    public static void register(RegisterCommandsEvent event) {
        event.getDispatcher().register(literal("bingo").then(
                 literal("vip").requires(cs -> cs.hasPermission(2)).then(argument("players", players()).executes(new VipCommand()))
        ).then(
                literal("unvip").requires(cs -> cs.hasPermission(2)).then(argument("players", players()).executes(new UnvipCommand()))
        ).then(
                literal("vipteam").requires(cs -> cs.hasPermission(2)).then(argument("team", enumArgument(DyeColor.class)).executes(new VipTeamCommand()))
        ).then(
                literal("unvipteam").requires(cs -> cs.hasPermission(2)).then(argument("team", enumArgument(DyeColor.class)).executes(new UnvipTeamCommand()))
        ).then(
                literal("maxplayers").requires(cs -> cs.hasPermission(2)).then(argument("maxPlayers", integer(-1)).executes(new MaxPlayersCommand()))
        ).then(
                literal("countdown").requires(cs -> cs.hasPermission(2)).then(argument("countdown", integer(-1)).executes(new CountdownCommand()))
        ));
    }
}
