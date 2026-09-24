package com.keyshow;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

/** Glue for Minecraft 26.x. */
final class Platform {
    private static final KeyMapping.Category CATEGORY =
            KeyMapping.Category.register(Identifier.fromNamespaceAndPath("keycps", "keybinds"));

    private Platform() {
    }

    static KeyMapping key(String name, int glfwKey) {
        return KeyMappingHelper.registerKeyMapping(new KeyMapping(name, InputConstants.Type.KEYSYM, glfwKey, CATEGORY));
    }

    static void registerHud() {
        HudElementRegistry.addLast(Identifier.fromNamespaceAndPath("keycps", "hud"), (graphics, delta) ->
                KeyShowHud.renderInGame(new GuiCanvas(graphics, Minecraft.getInstance().font)));
    }

    static void chat(Minecraft mc, Component message) {
        mc.gui.getChat().addClientSystemMessage(message);
    }
}
