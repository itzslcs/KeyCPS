package com.keyshow;

import com.keyshow.KeyTracker.Tracked;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;

/** Draws the keystrokes + CPS overlay. All sizes are in unscaled HUD pixels; the canvas applies the scale. */
public class KeyShowHud {
    private static final int K = 22;
    private static final int G = 2;
    private static final int SMALL_ROW = 14;
    private static final float SMALL_TEXT = 0.75F;

    /** Where the HUD was last drawn, in GUI pixels (used by the move screen for hit-testing). */
    public static int lastX;
    public static int lastY;
    public static int lastW;
    public static int lastH;
    private static long lastFrameNs;

    /** HUD callback: draws in-game unless one of our own screens is already showing the preview. */
    public static void renderInGame(Canvas c) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || Platform.hudHidden(mc) || !KeyShowConfig.get().enabled || Platform.screen(mc) instanceof BaseScreen) {
            return;
        }
        draw(c, mc.getWindow().getGuiScaledWidth(), mc.getWindow().getGuiScaledHeight());
    }

    /** Draws the HUD at its configured position, kept inside the screen. */
    public static void draw(Canvas c, int screenW, int screenH) {
        KeyShowConfig cfg = KeyShowConfig.get();
        long now = System.nanoTime();
        float step = lastFrameNs == 0 ? 1 : Math.min(1, (now - lastFrameNs) / 1e9F * 14);
        lastFrameNs = now;

        int w = width(c, cfg);
        int h = height(cfg);
        lastW = Math.round(w * cfg.scale);
        lastH = Math.round(h * cfg.scale);
        lastX = clamp(cfg.x, screenW - lastW);
        lastY = clamp(cfg.y, screenH - lastH);
        if (h <= 0) {
            return;
        }

        c.push(lastX, lastY, cfg.scale);
        int y = 0;
        if (cfg.showMovement) {
            key(c, cfg, KeyTracker.FORWARD, (w - K) / 2, y, K, K, step, false);
            y += K + G;
            int ax = (w - (3 * K + 2 * G)) / 2;
            key(c, cfg, KeyTracker.LEFT, ax, y, K, K, step, false);
            key(c, cfg, KeyTracker.BACK, ax + K + G, y, K, K, step, false);
            key(c, cfg, KeyTracker.RIGHT, ax + 2 * (K + G), y, K, K, step, false);
            y += K + G;
        }
        if (cfg.showJump) {
            key(c, cfg, KeyTracker.JUMP, 0, y, w, SMALL_ROW - 2, step, false);
            y += SMALL_ROW - 2 + G;
        }
        if (cfg.showMouse) {
            int mw = (w - G) / 2;
            int mh = cfg.showCps ? K + 4 : K;
            key(c, cfg, KeyTracker.ATTACK, 0, y, mw, mh, step, cfg.showCps);
            key(c, cfg, KeyTracker.USE, mw + G, y, w - mw - G, mh, step, cfg.showCps);
            y += mh + G;
        }
        if (cfg.showSneakSprint) {
            int mw = (w - G) / 2;
            key(c, cfg, KeyTracker.SNEAK, 0, y, mw, SMALL_ROW, step, false);
            key(c, cfg, KeyTracker.SPRINT, mw + G, y, w - mw - G, SMALL_ROW, step, false);
        }
        c.pop();
    }

    private static int width(Canvas c, KeyShowConfig cfg) {
        int w = 3 * K + 2 * G;
        if (cfg.showMouse) {
            int label = Math.max(c.width(label(cfg, KeyTracker.ATTACK)), c.width(label(cfg, KeyTracker.USE)));
            w = Math.max(w, 2 * (label + 8) + G);
        }
        if (cfg.showSneakSprint) {
            int label = Math.max(c.width(KeyTracker.SNEAK.label()), c.width(KeyTracker.SPRINT.label()));
            w = Math.max(w, 2 * (label + 8) + G);
        }
        return w;
    }

    private static int height(KeyShowConfig cfg) {
        int h = 0;
        if (cfg.showMovement) h += 2 * (K + G);
        if (cfg.showJump) h += SMALL_ROW - 2 + G;
        if (cfg.showMouse) h += (cfg.showCps ? K + 4 : K) + G;
        if (cfg.showSneakSprint) h += SMALL_ROW + G;
        return h - G;
    }

    private static void key(Canvas c, KeyShowConfig cfg, Tracked t, int x, int y, int w, int h, float step,
                            boolean cpsLine) {
        float target = t.down() ? 1 : 0;
        t.glow = cfg.fade ? t.glow + (target - t.glow) * step : target;

        int offBg = cfg.showBg ? (cfg.bgAlpha & 0xFF) << 24 | cfg.bgColor & 0xFFFFFF : 0;
        int onBg = 0xE6000000 | cfg.pressedFill & 0xFFFFFF;
        box(c, cfg, x, y, x + w, y + h, lerp(offBg, onBg, t.glow));

        int offText = cfg.rainbow ? rainbow(x + y) : 0xFF000000 | cfg.textColor;
        int text = lerp(offText, contrast(cfg.pressedFill), t.glow);
        boolean shadow = cfg.textShadow && t.glow < 0.5F;
        int rate = t.rate();

        String secondary = null;
        int secondaryColor = text;
        if (cpsLine) {
            secondary = rate + " CPS";
            if (cfg.cpsWarn > 0 && rate >= cfg.cpsWarn) {
                secondaryColor = 0xFFFF5555;
            }
        } else if (cfg.showKeyRates && rate > 0 && h >= K) {
            secondary = String.valueOf(rate);
        }

        String label = label(cfg, t);
        if (t == KeyTracker.JUMP && KeyTracker.boundKey(t.mapping()).getValue() == InputConstants.KEY_SPACE) {
            // Draw the space bar as a line, like on a keyboard.
            int lw = w / 3;
            int ly = y + h / 2;
            if (shadow) c.fill(x + (w - lw) / 2 + 1, ly + 1, x + (w + lw) / 2 + 1, ly + 2, (text & 0xFCFCFC) >> 2 | 0xFF000000);
            c.fill(x + (w - lw) / 2, ly, x + (w + lw) / 2, ly + 1, text);
            return;
        }

        int cx = x + w / 2;
        if (secondary == null) {
            c.centered(label, cx, y + (h - 7) / 2, text, shadow);
        } else {
            c.centered(label, cx, y + (h - 14) / 2, text, shadow);
            c.push(cx, y + (h - 14) / 2 + 9, SMALL_TEXT);
            c.centered(secondary, 0, 0, secondaryColor, shadow);
            c.pop();
        }
    }

    private static String label(KeyShowConfig cfg, Tracked t) {
        if (cfg.alwaysMouseLabels && t == KeyTracker.ATTACK) return "LMB";
        if (cfg.alwaysMouseLabels && t == KeyTracker.USE) return "RMB";
        return t.label();
    }

    /** A filled box, optionally with the corner pixels cut off so it looks rounded. */
    private static void box(Canvas c, KeyShowConfig cfg, int x1, int y1, int x2, int y2, int argb) {
        if ((argb >>> 24) == 0) {
            return;
        }
        if (!cfg.rounded) {
            c.fill(x1, y1, x2, y2, argb);
            return;
        }
        c.fill(x1 + 1, y1, x2 - 1, y2, argb);
        c.fill(x1, y1 + 1, x1 + 1, y2 - 1, argb);
        c.fill(x2 - 1, y1 + 1, x2, y2 - 1, argb);
    }

    private static int clamp(int v, int max) {
        return Math.max(0, Math.min(v, Math.max(0, max)));
    }

    static int lerp(int a, int b, float t) {
        if (t <= 0) return a;
        if (t >= 1) return b;
        int out = 0;
        for (int shift = 0; shift < 32; shift += 8) {
            int ca = a >>> shift & 0xFF;
            int cb = b >>> shift & 0xFF;
            out |= Math.round(ca + (cb - ca) * t) << shift;
        }
        return out;
    }

    /** Black or white, whichever reads better on the given colour. */
    static int contrast(int rgb) {
        double lum = 0.299 * (rgb >> 16 & 0xFF) + 0.587 * (rgb >> 8 & 0xFF) + 0.114 * (rgb & 0xFF);
        return lum > 140 ? 0xFF000000 : 0xFFFFFFFF;
    }

    private static int rainbow(int offset) {
        float hue = ((System.currentTimeMillis() % 4000L) / 4000F + offset / 200F) % 1F;
        float h6 = hue * 6;
        float f = h6 - (int) h6;
        int q = Math.round(255 * (1 - f));
        int t = Math.round(255 * f);
        int rgb = switch ((int) h6) {
            case 0 -> 255 << 16 | t << 8;
            case 1 -> q << 16 | 255 << 8;
            case 2 -> 255 << 8 | t;
            case 3 -> q << 8 | 255;
            case 4 -> t << 16 | 255;
            default -> 255 << 16 | q;
        };
        return 0xFF000000 | rgb;
    }
}
