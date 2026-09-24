package com.keyshow;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

/** Glue for the Minecraft versions this folder is built for (see versions/*.properties). */
final class Platform {
    private static final KeyMapping.Category CATEGORY =
            KeyMapping.Category.register(Identifier.fromNamespaceAndPath("keycps", "keybinds"));
    private Platform() {
    }

    static KeyMapping key(String name, int key) {
        return KeyBindingHelper.registerKeyBinding(new KeyMapping(name, key, CATEGORY));
    }

    static void registerHud() {
        HudRenderCallback.EVENT.register((graphics, delta) ->
                KeyShowHud.renderInGame(new GuiCanvas(graphics, Minecraft.getInstance().font)));
    }

    static void chat(Minecraft mc, Component message) {
        mc.gui.getChat().addMessage(message);
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
