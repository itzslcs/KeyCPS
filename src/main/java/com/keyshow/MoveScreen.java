package com.keyshow;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/** Drag the HUD with the mouse; it snaps to the screen edges and centre. Esc saves. */
public class MoveScreen extends BaseScreen {
    private static final int SNAP = 6;
    private static final int MARGIN = 4;
    private final Screen parent;
    private boolean dragging;
    private double grabX;
    private double grabY;

    public MoveScreen(Screen parent) {
        super(Component.translatable("keycps.move.title"));
        this.parent = parent;
    }

    @Override
    protected boolean dimBackground() {
        return false;
    }

    @Override
    protected void drawOverlay(Canvas c) {
        KeyShowHud.draw(c, this.width, this.height);
        int x1 = KeyShowHud.lastX - 1;
        int y1 = KeyShowHud.lastY - 1;
        int x2 = KeyShowHud.lastX + KeyShowHud.lastW + 1;
        int y2 = KeyShowHud.lastY + KeyShowHud.lastH + 1;
        int col = dragging ? 0xFFFFFF55 : 0xFF55FF55;
        c.fill(x1, y1, x2, y1 + 1, col);
        c.fill(x1, y2 - 1, x2, y2, col);
        c.fill(x1, y1, x1 + 1, y2, col);
        c.fill(x2 - 1, y1, x2, y2, col);
        c.centered(Component.translatable("keycps.move.hint").getString(), this.width / 2, this.height / 2 - 4, 0xFFFFFFFF, true);
    }

    @Override
    protected boolean onMousePress(double mx, double my) {
        if (mx >= KeyShowHud.lastX && mx < KeyShowHud.lastX + KeyShowHud.lastW
                && my >= KeyShowHud.lastY && my < KeyShowHud.lastY + KeyShowHud.lastH) {
            dragging = true;
            grabX = mx - KeyShowHud.lastX;
            grabY = my - KeyShowHud.lastY;
            return true;
        }
        return false;
    }

    @Override
    protected boolean onMouseDrag(double mx, double my) {
        if (!dragging) {
            return false;
        }
        KeyShowConfig cfg = KeyShowConfig.get();
        cfg.x = snap((int) Math.round(mx - grabX), KeyShowHud.lastW, this.width);
        cfg.y = snap((int) Math.round(my - grabY), KeyShowHud.lastH, this.height);
        return true;
    }

    @Override
    protected void onMouseRelease() {
        dragging = false;
    }

    private static int snap(int pos, int size, int screen) {
        if (Math.abs(pos - MARGIN) < SNAP) return MARGIN;
        if (Math.abs(pos + size - (screen - MARGIN)) < SNAP) return screen - MARGIN - size;
        if (Math.abs(pos + size / 2 - screen / 2) < SNAP) return screen / 2 - size / 2;
        return pos;
    }

    @Override
    public void onClose() {
        KeyShowConfig.save();
        Platform.setScreen(this.minecraft, this.parent);
    }
}
