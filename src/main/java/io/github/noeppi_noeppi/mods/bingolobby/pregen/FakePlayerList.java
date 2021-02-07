package io.github.noeppi_noeppi.mods.bingolobby.pregen;

import net.minecraft.server.management.PlayerList;

public class FakePlayerList extends PlayerList {

    public FakePlayerList(FakeServer server) {
        super(server, server.field_240767_f_, server.playerDataManager, 1);
    }
}
