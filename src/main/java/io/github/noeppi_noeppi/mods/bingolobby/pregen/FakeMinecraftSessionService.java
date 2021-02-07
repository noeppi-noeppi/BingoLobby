package io.github.noeppi_noeppi.mods.bingolobby.pregen;

import com.mojang.authlib.Agent;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.ProfileLookupCallback;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import com.mojang.authlib.minecraft.InsecureTextureException;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftSessionService;

import java.net.InetAddress;
import java.util.Map;

public class FakeMinecraftSessionService implements MinecraftSessionService, GameProfileRepository {

    public static final FakeMinecraftSessionService INSTANCE = new FakeMinecraftSessionService();
    
    private FakeMinecraftSessionService() {
        
    }
    
    @Override
    public void joinServer(GameProfile profile, String authenticationToken, String serverId) throws AuthenticationException {
        throw new AuthenticationException("The pregen server is not meant to be used by players.");
    }

    @Override
    public GameProfile hasJoinedServer(GameProfile user, String serverId, InetAddress address) throws AuthenticationUnavailableException {
        return null;
    }

    @Override
    public Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> getTextures(GameProfile profile, boolean requireSecure) {
        throw new InsecureTextureException("The pregen server is not meant to be used by players.");
    }

    @Override
    public GameProfile fillProfileProperties(GameProfile profile, boolean requireSecure) {
        return profile;
    }

    @Override
    public void findProfilesByNames(String[] names, Agent agent, ProfileLookupCallback callback) {
        for (String name : names) {
            callback.onProfileLookupFailed(new GameProfile(null, name), new IllegalStateException("The pregen server is not meant to be used by players."));
        }
    }
}
