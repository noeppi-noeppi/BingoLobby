package io.github.noeppi_noeppi.mods.bingolobby.pregen;

import net.minecraft.server.MinecraftServer;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.function.Supplier;

public class ServerPreTickQueue {
    
    // True means cancel that tick
    private static final Map<MinecraftServer, Queue<Supplier<Boolean>>> queues = new HashMap<>();
    
    public static Queue<Supplier<Boolean>> getQueue(MinecraftServer server) {
        if ( server instanceof FakeServer) {
            return new LinkedList<>();
        }
        return queues.computeIfAbsent(server, k -> new LinkedList<>());
    }
    
    public static void schedulePreTickTask(MinecraftServer server, Supplier<Boolean> task) {
        if (server instanceof FakeServer) {
            throw new IllegalStateException("Can't add pre tick schedule to bingo pregen fake server.");
        }
        getQueue(server).add(task);
    }
}
