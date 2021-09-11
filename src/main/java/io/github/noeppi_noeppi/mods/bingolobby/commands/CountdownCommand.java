package io.github.noeppi_noeppi.mods.bingolobby.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import io.github.noeppi_noeppi.mods.bingolobby.Lobby;
import io.github.noeppi_noeppi.mods.bongo.Bongo;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;

public class CountdownCommand implements Command<CommandSourceStack> {

    @Override
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        Bongo bongo = Bongo.get(player.getCommandSenderWorld());

        if (!bongo.active()) {
            throw new SimpleCommandExceptionType(new TranslatableComponent("bongo.cmd.start.notcreated")).create();
        } else if (bongo.running() || bongo.won()) {
            throw new SimpleCommandExceptionType(new TranslatableComponent("bongo.cmd.start.alreadyrunning")).create();
        }
        
        Lobby lobby = Lobby.get(player.getCommandSenderWorld());
        int countdown = context.getArgument("countdown", Integer.class);
        lobby.setCountdown(countdown);
        
        context.getSource().sendSuccess(new TranslatableComponent("bingolobby.countdown.set", Integer.toString(countdown / 60), Integer.toString(countdown % 60)), true);

        return 0;
    }
}
