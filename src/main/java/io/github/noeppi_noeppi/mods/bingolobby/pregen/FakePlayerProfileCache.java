package io.github.noeppi_noeppi.mods.bingolobby.pregen;

import net.minecraft.server.management.PlayerProfileCache;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FakePlayerProfileCache extends PlayerProfileCache {

    private FakePlayerProfileCache() throws IOException {
        super(FakeMinecraftSessionService.INSTANCE, File.createTempFile("bingo-pregen", "profile-cache"));
    }

    @Override
    public List<PlayerProfileCache.ProfileEntry> func_242116_a() {
        return new ArrayList<>();
    }

    @Override
    public void save() {
        //
    }

    public static FakePlayerProfileCache create() {
        try {
            return new FakePlayerProfileCache();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
