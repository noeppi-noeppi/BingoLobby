package io.github.noeppi_noeppi.mods.bingolobby.compat;

import de.melanx.skyblockbuilder.util.CompatHelper;
import io.github.noeppi_noeppi.mods.bingolobby.BingoLobby;

public class SkyblockIntegration {

    public static void setup() {
        CompatHelper.disableSpawnTeleport(BingoLobby.getInstance().modid);
    }
}
