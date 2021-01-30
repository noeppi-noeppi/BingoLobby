package io.github.noeppi_noeppi.mods.bingolobby;

import io.github.noeppi_noeppi.libx.mod.registration.ModXRegistration;
import io.github.noeppi_noeppi.mods.bingolobby.commands.LobbyCommands;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import javax.annotation.Nonnull;

@Mod("bingolobby")
public class BingoLobby extends ModXRegistration {
    
    private static BingoLobby instance;
    
    public BingoLobby() {
        super("bingolobby", null);

        instance = this;

        this.addRegistrationHandler(ModBiomes::init);
        this.addRegistrationHandler(ModDimensions::init);

        MinecraftForge.EVENT_BUS.addListener(LobbyCommands::register);
        MinecraftForge.EVENT_BUS.register(new DestinationControlEvents());
        MinecraftForge.EVENT_BUS.register(new LobbyEvents());
        MinecraftForge.EVENT_BUS.register(new BongoEvents());
        MinecraftForge.EVENT_BUS.register(new AssignTeamEvents());
    }
    
    @Nonnull
    public static BingoLobby getInstance() {
        return instance;
    }
    
    @Override
    protected void setup(FMLCommonSetupEvent event) {

    }

    @Override
    protected void clientSetup(FMLClientSetupEvent event) {

    }
}

