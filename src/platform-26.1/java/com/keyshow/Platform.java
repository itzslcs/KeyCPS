package com.keyshow;

import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

/** Glue for Minecraft 26.1 - 26.1.2. */
final class Platform {
    private static final KeyMapping.Category CATEGORY =
            KeyMapping.Category.register(Identifier.fromNamespaceAndPath("keycps", "keybinds"));

    private Platform() {
    }

    static KeyMapping key(String name, int key) {
        return KeyMappingHelper.registerKeyMapping(new KeyMapping(name, key, CATEGORY));
    }

    static void registerHud() {
        HudElementRegistry.addLast(Identifier.fromNamespaceAndPath("keycps", "hud"), (graphics, delta) ->
                KeyShowHud.renderInGame(new GuiCanvas(graphics, Minecraft.getInstance().font)));
    }

    static void chat(Minecraft mc, Component message) {
        mc.gui.getChat().addClientSystemMessage(message);
    }

    static Screen screen(Minecraft mc) {
        return mc.screen;
    }

    static void setScreen(Minecraft mc, Screen screen) {
        mc.setScreen(screen);
    }

    static boolean hudHidden(Minecraft mc) {
        return mc.options.hideGui;
    }
}
