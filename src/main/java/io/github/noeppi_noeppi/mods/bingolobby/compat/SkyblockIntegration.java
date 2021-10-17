package io.github.noeppi_noeppi.mods.bingolobby.compat;

import de.melanx.skyblockbuilder.api.SkyblockBuilderAPI;
import io.github.noeppi_noeppi.mods.bingolobby.BingoLobby;

public class SkyblockIntegration {

    public static void setup() {
        SkyblockBuilderAPI.disableSpawnTeleport(BingoLobby.getInstance().modid);
    }
}
