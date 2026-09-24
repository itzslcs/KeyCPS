package com.keyshow;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

/** Glue for the Minecraft versions this folder is built for (see versions/*.properties). */
final class Platform {
    private static final String CATEGORY = "key.categories.keycps.keybinds";
    private Platform() {
    }

    static KeyMapping key(String name, int glfwKey) {
        return KeyBindingHelper.registerKeyBinding(new KeyMapping(name, InputConstants.Type.KEYSYM, glfwKey, CATEGORY));
    }

    static void registerHud() {
        HudRenderCallback.EVENT.register((graphics, delta) ->
                KeyShowHud.renderInGame(new GuiCanvas(graphics, Minecraft.getInstance().font)));
    }

    static void chat(Minecraft mc, Component message) {
        mc.gui.getChat().addMessage(message);
    }
}
