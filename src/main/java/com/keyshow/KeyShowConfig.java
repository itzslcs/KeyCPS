package com.keyshow;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.nio.file.Files;
import java.nio.file.Path;
import net.fabricmc.loader.api.FabricLoader;

/** Settings, saved as config/keycps.json. */
public class KeyShowConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve("keycps.json");
    private static KeyShowConfig INSTANCE = new KeyShowConfig();

    public float scale = 1.0F;
    public int offsetX = 0;
    public int offsetY = 0;
    public boolean compactLayout = false;
    public boolean showWasd = true;
    public boolean showJump = true;
    public boolean showCps = true;
    public boolean showDivider = true;
    public boolean combineCps = false;
    public boolean showBg = true;
    public boolean useColorBg = false;
    public boolean colorPressedText = false;
    public int bgAlpha = 128;
    public int bgColor = 0x000000;
    public int textColor = 0xFFFFFF;
    public int cpsColor = 0xFFFFFF;
    public int pressedColor = 0x000000;

    public static KeyShowConfig get() {
        return INSTANCE;
    }

    public static void load() {
        try {
            if (Files.exists(PATH)) {
                KeyShowConfig c = GSON.fromJson(Files.readString(PATH), KeyShowConfig.class);
                if (c != null) {
                    INSTANCE = c;
                }
            } else {
                save();
            }
        } catch (Exception e) {
            KeyShowMod.LOGGER.warn("[KeyCPS] config unreadable, using defaults", e);
        }
    }

    public static void save() {
        try {
            Files.createDirectories(PATH.getParent());
            Files.writeString(PATH, GSON.toJson(INSTANCE));
        } catch (Exception e) {
            KeyShowMod.LOGGER.warn("[KeyCPS] could not save config", e);
        }
    }
}
