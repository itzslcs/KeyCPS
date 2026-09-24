package com.keyshow;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KeyShowMod implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("keycps");
    public static KeyMapping moveHudKey;

    @Override
    public void onInitializeClient() {
        KeyShowConfig.load();

        moveHudKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.keycps.move_hud",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_UNKNOWN, // unbound by default
                "key.categories.keycps.keybinds"));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            KeyTracker.tick();
            while (moveHudKey.consumeClick()) {
                KeyShowHud.startDrag(client);
            }
        });

        HudRenderCallback.EVENT.register(KeyShowHud::render);
        LOGGER.info("[KeyCPS] ready");
    }
}
