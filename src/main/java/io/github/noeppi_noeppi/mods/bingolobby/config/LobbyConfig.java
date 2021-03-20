package io.github.noeppi_noeppi.mods.bingolobby.config;

import com.google.common.collect.ImmutableList;
import io.github.noeppi_noeppi.libx.config.Config;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;

import java.util.List;

public class LobbyConfig {
    
    @Config("The title to be shown in the sidebar while in the lobby.")
    public static IFormattableTextComponent title = new StringTextComponent("Bongo");
    
    @Config(
            value = {"The subtitle to be shown in the sidebar while in the lobby.", "This will cycle through all the elements in the list."},
            elementType = IFormattableTextComponent.class
    )
    public static List<IFormattableTextComponent> subtitle = ImmutableList.of(new StringTextComponent("Mod by noeppi_noeppi"));

    @Config(
            value = {"The second subtitle to be shown in the sidebar while in the lobby.", "This will cycle through all the elements in the list."},
            elementType = IFormattableTextComponent.class
    )
    public static List<IFormattableTextComponent> subtitle2 = ImmutableList.of();

    @Config({"Whether subtitle 1 or subtitle 2 should be replaced with the countdown", "If a subtitle is empty it won't get replaced and the countdown won't be visible.", "True means subtitle 2, false means subtitle 1"})
    public static boolean countdown_in_subtitle2 = false;

    @Config("Whether the world is being generated as void world or not")
    public static boolean isVoid = false;
}
