package io.github.noeppi_noeppi.mods.bingolobby;

import io.github.noeppi_noeppi.mods.bingolobby.commands.LobbyCommands;
import io.github.noeppi_noeppi.mods.bingolobby.compat.SkyblockIntegration;
import io.github.noeppi_noeppi.mods.bingolobby.datagen.BingoLobbyBiomes;
import io.github.noeppi_noeppi.mods.bingolobby.datagen.BingoLobbyDimensionTypes;
import io.github.noeppi_noeppi.mods.bingolobby.datagen.BingoLobbyDimensions;
import io.github.noeppi_noeppi.mods.bingolobby.network.LobbyNetwork;
import io.github.noeppi_noeppi.mods.bingolobby.render.RenderOverlay;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.moddingx.libx.datagen.DatagenSystem;
import org.moddingx.libx.mod.ModXRegistration;
import org.moddingx.libx.registration.RegistrationBuilder;

import javax.annotation.Nonnull;

@Mod("bingolobby")
public final class BingoLobby extends ModXRegistration {
    
    private static BingoLobby instance;
    private static LobbyNetwork network;
    
    public BingoLobby() {
        instance = this;
        network = new LobbyNetwork(this);

        this.addRegistrationHandler(ModDimensions::init);

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerGUIs));
        
        MinecraftForge.EVENT_BUS.addListener(LobbyCommands::register);
        MinecraftForge.EVENT_BUS.register(new DestinationControlEvents());
        MinecraftForge.EVENT_BUS.register(new LobbyEvents());
        MinecraftForge.EVENT_BUS.register(new BongoEvents());
        MinecraftForge.EVENT_BUS.register(new EventListener());

        DatagenSystem.create(this, system -> {
            system.addRegistryProvider(BingoLobbyBiomes::new);
            system.addRegistryProvider(BingoLobbyDimensionTypes::new);
            system.addRegistryProvider(BingoLobbyDimensions::new);
        });
    }
    
    @Nonnull
    public static BingoLobby getInstance() {
        return instance;
    }

    public static LobbyNetwork getNetwork() {
        return network;
    }

    @Override
    protected void initRegistration(RegistrationBuilder builder) {
        //
    }

    @Override
    protected void setup(FMLCommonSetupEvent event) {
        if (ModList.get().isLoaded("skyblockbuilder")) {
            SkyblockIntegration.setup();
        }
    }

    @Override
    protected void clientSetup(FMLClientSetupEvent event) {

    }

    @OnlyIn(Dist.CLIENT)
    private void registerGUIs(RegisterGuiOverlaysEvent event) {
        event.registerAbove(VanillaGuiOverlay.CHAT_PANEL.id(), "bongo", RenderOverlay.INSTANCE);
    }
}
