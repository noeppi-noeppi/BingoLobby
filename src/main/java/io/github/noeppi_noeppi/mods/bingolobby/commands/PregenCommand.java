package io.github.noeppi_noeppi.mods.bingolobby.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import io.github.noeppi_noeppi.mods.bingolobby.pregen.ChunkPregenerator;
import net.minecraft.command.CommandSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class PregenCommand implements Command<CommandSource> {
    
    @Override
    public int run(CommandContext<CommandSource> context) throws CommandSyntaxException {
        MinecraftServer server = context.getSource().getServer();
        long seed = server.func_241755_D_().getSeed();
        ServerWorld nether = server.getWorld(World.THE_NETHER);
        if (nether != null) {
            if (seed != nether.getSeed()) throw new SimpleCommandExceptionType(new TranslationTextComponent("bingolobby.pregen.seed_mismatch")).create();

        }
        ChunkPregenerator.pregenerateChunks(server, seed, 24);
        this.feedback(context.getSource(), 24, seed);
        return 0;
    }
    
    private void feedback(CommandSource source, @SuppressWarnings("SameParameterValue") int range, long seed) {
        source.sendFeedback(new TranslationTextComponent("bingolobby.pregen.started", Integer.toString(range * range), Integer.toString(range * range * 1024), Long.toString(seed)), true);
    }
}
