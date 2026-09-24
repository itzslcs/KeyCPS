package com.keyshow;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KeyShowMod implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("keycps");
    public static KeyMapping openConfigKey;
    public static KeyMapping moveHudKey;
    public static KeyMapping toggleHudKey;

    @Override
    public void onInitializeClient() {
        KeyShowConfig.load();

        openConfigKey = Platform.key("key.keycps.open_config", GLFW.GLFW_KEY_RIGHT_SHIFT);
        moveHudKey = Platform.key("key.keycps.move_hud", GLFW.GLFW_KEY_UNKNOWN);
        toggleHudKey = Platform.key("key.keycps.toggle_hud", GLFW.GLFW_KEY_UNKNOWN);

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openConfigKey.consumeClick()) {
                client.setScreen(new ConfigScreen(null));
            }
            while (moveHudKey.consumeClick()) {
                client.setScreen(new MoveScreen(null));
            }
            while (toggleHudKey.consumeClick()) {
                KeyShowConfig cfg = KeyShowConfig.get();
                cfg.enabled = !cfg.enabled;
                KeyShowConfig.save();
            }
            KeyShowConfig cfg = KeyShowConfig.get();
            if (!cfg.welcomeShown && client.player != null) {
                cfg.welcomeShown = true;
                KeyShowConfig.save();
                Platform.chat(client, Component.translatable("keycps.welcome",
                        openConfigKey.getTranslatedKeyMessage().copy().withStyle(net.minecraft.ChatFormatting.YELLOW)));
            }
        });

        Platform.registerHud();
        LOGGER.info("[KeyCPS] ready");
    }
}
