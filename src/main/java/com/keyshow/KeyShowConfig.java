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

    public boolean enabled = true;
    /** Top-left corner of the HUD in GUI pixels. New installs start in the top-left corner. */
    public int x = 4;
    public int y = 4;
    public float scale = 1.0F;

    public boolean showMovement = true;
    public boolean showJump = true;
    public boolean showMouse = true;
    public boolean showCps = true;
    public boolean showSneakSprint = false;
    public boolean showKeyRates = false;
    /** CPS at or above this turns the counter red. 0 = off. */
    public int cpsWarn = 0;

    public boolean showBg = true;
    public int bgAlpha = 110;
    public int bgColor = 0x000000;
    public int textColor = 0xFFFFFF;
    public int pressedFill = 0xFFFFFF;
    public boolean rounded = true;
    public boolean textShadow = true;
    public boolean rainbow = false;
    public boolean fade = true;

    public boolean welcomeShown = false;

    public static KeyShowConfig get() {
        return INSTANCE;
    }

    public static void reset() {
        KeyShowConfig fresh = new KeyShowConfig();
        fresh.welcomeShown = INSTANCE.welcomeShown;
        INSTANCE = fresh;
        save();
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
