package com.keyshow;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;
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

        openConfigKey = Platform.key("key.keycps.open_config", InputConstants.KEY_RSHIFT);
        moveHudKey = Platform.key("key.keycps.move_hud", InputConstants.UNKNOWN.getValue());
        toggleHudKey = Platform.key("key.keycps.toggle_hud", InputConstants.UNKNOWN.getValue());

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openConfigKey.consumeClick()) {
                Platform.setScreen(client, new ConfigScreen(null));
            }
            while (moveHudKey.consumeClick()) {
                Platform.setScreen(client, new MoveScreen(null));
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
