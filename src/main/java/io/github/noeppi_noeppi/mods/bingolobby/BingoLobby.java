package io.github.noeppi_noeppi.mods.bingolobby;

import io.github.noeppi_noeppi.libx.config.ConfigManager;
import io.github.noeppi_noeppi.libx.mod.registration.ModXRegistration;
import io.github.noeppi_noeppi.mods.bingolobby.commands.LobbyCommands;
import io.github.noeppi_noeppi.mods.bingolobby.config.LobbyConfig;
import io.github.noeppi_noeppi.mods.bingolobby.network.LobbyNetwork;
import io.github.noeppi_noeppi.mods.bingolobby.render.RenderOverlay;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import javax.annotation.Nonnull;

@Mod("bingolobby")
public class BingoLobby extends ModXRegistration {
    
    private static BingoLobby instance;
    private static LobbyNetwork network;
    
    public BingoLobby() {
        super("bingolobby", null);

        ConfigManager.registerConfig(this.modid, LobbyConfig.class, false);

        instance = this;
        network = new LobbyNetwork(this);

        this.addRegistrationHandler(ModBiomes::init);
        this.addRegistrationHandler(ModDimensions::init);

        MinecraftForge.EVENT_BUS.addListener(LobbyCommands::register);
        MinecraftForge.EVENT_BUS.register(new DestinationControlEvents());
        MinecraftForge.EVENT_BUS.register(new LobbyEvents());
        MinecraftForge.EVENT_BUS.register(new BongoEvents());
        MinecraftForge.EVENT_BUS.register(new EventListener());
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> MinecraftForge.EVENT_BUS.register(new RenderOverlay()));
    }
    
    @Nonnull
    public static BingoLobby getInstance() {
        return instance;
    }

    public static LobbyNetwork getNetwork() {
        return network;
    }

    @Override
    protected void setup(FMLCommonSetupEvent event) {

    }

    @Override
    protected void clientSetup(FMLClientSetupEvent event) {

    }
}

