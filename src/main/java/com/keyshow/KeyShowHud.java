package com.keyshow;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

/** Draws the keystrokes + CPS overlay. */
public class KeyShowHud {
    private static final int KEY_SZ = 26;
    public static boolean moving = false;

    public static void startDrag(Minecraft mc) {
        moving = true;
        mc.setScreen(new DragScreen());
    }

    public static void render(GuiGraphics ctx, DeltaTracker delta) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.options.hideGui) {
            return;
        }

        KeyTracker.pollMouse(KeyTracker.windowHandle(mc));
        KeyShowConfig cfg = KeyShowConfig.get();
        Font tr = mc.font;
        int sw = mc.getWindow().getGuiScaledWidth();
        int sh = mc.getWindow().getGuiScaledHeight();

        float scale = cfg.scale;
        boolean compact = cfg.compactLayout;
        boolean showWasd = cfg.showWasd;
        boolean showJump = cfg.showJump && !compact;
        boolean showCps = cfg.showCps;
        boolean showDivider = cfg.showDivider && compact;
        boolean combineCps = cfg.combineCps;
        boolean colorPress = cfg.colorPressedText;

        int cBgOn = 0xFFFFFFFF;
        int cBgOff = !cfg.showBg ? 0 : cfg.bgAlpha << 24 | (cfg.useColorBg ? cfg.bgColor & 0xFFFFFF : 0);
        int cTextOff = 0xFF000000 | cfg.textColor & 0xFFFFFF;
        int cTextCps = 0xFF000000 | cfg.cpsColor & 0xFFFFFF;
        int cTextPress = 0xFF000000 | cfg.pressedColor & 0xFFFFFF;

        int gap = Math.max(1, (int) ((compact ? 1 : 2) * scale));
        int k = Math.max(1, (int) (KEY_SZ * scale));
        int sr = Math.max(1, (int) (9.0F * scale));
        int ss = Math.max(2, (int) (6.0F * scale));
        int dividerH = Math.max(1, (int) scale);
        int pad = Math.max(2, (int) (8.0F * scale));

        String atkLabel = KeyTracker.getAttackLabel();
        String useLabel = KeyTracker.getUseLabel();
        int wasdW = 3 * k + 2 * gap;
        int atkW = Math.max(k, tr.width(atkLabel) + 2 * pad);
        int useW = Math.max(2 * k + gap, tr.width(useLabel) + 2 * pad);
        int actionW = atkW + gap + useW;
        int totalW = Math.max(wasdW, actionW);

        String atkCpsTxt = KeyTracker.getAttackCps() + "";
        String useCpsTxt = KeyTracker.getUseCps() + "";
        String combinedCpsTxt = atkCpsTxt + " | " + useCpsTxt + " CPS";

        int totalH = (showWasd ? 2 * (k + gap) : 0)
                + (showJump ? ss + gap : 0)
                + k
                + (showDivider ? gap + dividerH : 0)
                + (showCps ? gap + sr : 0);
        int ox = sw / 2 - totalW / 2 + cfg.offsetX;
        int oy = sh - totalH - 14 + cfg.offsetY;
        int curY = oy;

        if (showWasd) {
            key(ctx, tr, ox + (totalW - k) / 2, oy, k, k, "W", KeyTracker.forward, cBgOn, cBgOff, cTextOff, cTextPress, colorPress);
            int ax = ox + (totalW - wasdW) / 2;
            curY = oy + k + gap;
            key(ctx, tr, ax, curY, k, k, "A", KeyTracker.left, cBgOn, cBgOff, cTextOff, cTextPress, colorPress);
            key(ctx, tr, ax + k + gap, curY, k, k, "S", KeyTracker.back, cBgOn, cBgOff, cTextOff, cTextPress, colorPress);
            key(ctx, tr, ax + 2 * (k + gap), curY, k, k, "D", KeyTracker.right, cBgOn, cBgOff, cTextOff, cTextPress, colorPress);
            curY += k + gap;
        }

        if (showJump) {
            ctx.fill(ox, curY, ox + totalW, curY + ss, KeyTracker.jumping ? cBgOn : cBgOff);
            curY += ss + gap;
        }

        int actX = ox + (totalW - actionW) / 2;
        key(ctx, tr, actX, curY, atkW, k, atkLabel, KeyTracker.attacking, cBgOn, cBgOff, cTextOff, cTextPress, colorPress);
        key(ctx, tr, actX + atkW + gap, curY, useW, k, useLabel, KeyTracker.using, cBgOn, cBgOff, cTextOff, cTextPress, colorPress);
        curY += k;

        if (showDivider) {
            curY += gap;
            ctx.fill(ox, curY, ox + totalW, curY + dividerH, cBgOn);
            curY += dividerH;
        }

        if (showCps) {
            curY += gap;
            if (combineCps) {
                ctx.drawCenteredString(tr, combinedCpsTxt, ox + totalW / 2, curY, cTextCps);
            } else {
                ctx.drawCenteredString(tr, atkCpsTxt + " CPS", actX + atkW / 2, curY, cTextCps);
                ctx.drawCenteredString(tr, useCpsTxt + " CPS", actX + atkW + gap + useW / 2, curY, cTextCps);
            }
        }

        if (moving) {
            // Outline while the HUD is being dragged.
            int x2 = ox + totalW;
            int y2 = oy + totalH;
            int c = 0xFF88CC88;
            ctx.fill(ox, oy, x2, oy + 1, c);
            ctx.fill(ox, y2 - 1, x2, y2, c);
            ctx.fill(ox, oy, ox + 1, y2, c);
            ctx.fill(x2 - 1, oy, x2, y2, c);
        }
    }

    private static void key(GuiGraphics ctx, Font tr, int x, int y, int w, int h, String label, boolean pressed,
                            int bgOn, int bgOff, int textOff, int textPress, boolean colorPress) {
        int bg = colorPress ? bgOff : (pressed ? bgOn : bgOff);
        ctx.fill(x, y, x + w, y + h, bg);
        int color = colorPress ? (pressed ? textPress : textOff) : (pressed ? 0xFF000000 : textOff);
        ctx.drawCenteredString(tr, label, x + w / 2, y + (h - 8) / 2 + 1, color);
    }
}
