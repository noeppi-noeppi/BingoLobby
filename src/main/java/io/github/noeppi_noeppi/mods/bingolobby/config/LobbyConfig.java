package io.github.noeppi_noeppi.mods.bingolobby.config;

import org.moddingx.libx.annotation.config.RegisterConfig;
import org.moddingx.libx.config.Config;
import net.minecraft.network.chat.Component;

import java.util.List;

@RegisterConfig
public class LobbyConfig {
    
    @Config("The title to be shown in the sidebar while in the lobby.")
    public static Component title = Component.literal("Bongo");
    
    @Config({
            "The subtitle to be shown in the sidebar while in the lobby.",
            "This will cycle through all the elements in the list."
    })
    public static List<Component> subtitle = List.of(Component.literal("Mod by noeppi_noeppi"));

    @Config({
            "The second subtitle to be shown in the sidebar while in the lobby.",
            "This will cycle through all the elements in the list."
    })
    public static List<Component> subtitle2 = List.of();

    @Config({"Whether subtitle 1 or subtitle 2 should be replaced with the countdown", "If a subtitle is empty it won't get replaced and the countdown won't be visible.", "True means subtitle 2, false means subtitle 1"})
    public static boolean countdown_in_subtitle2 = false;

    @Config("Whether the world is being generated as void world or not")
    public static boolean is_void = false;
}
