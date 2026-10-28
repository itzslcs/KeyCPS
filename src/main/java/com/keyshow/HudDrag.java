package com.keyshow;

/** Dragging the HUD preview with the mouse. Snaps to the screen edges and centre. Used by the settings and move screens. */
final class HudDrag {
    private static final int SNAP = 6;
    private static final int MARGIN = 4;
    private boolean dragging;
    private double grabX;
    private double grabY;

    boolean dragging() {
        return dragging;
    }

    /** Starts a drag if (mx, my) is on the HUD. */
    boolean press(double mx, double my) {
        if (mx >= KeyShowHud.lastX && mx < KeyShowHud.lastX + KeyShowHud.lastW
                && my >= KeyShowHud.lastY && my < KeyShowHud.lastY + KeyShowHud.lastH) {
            dragging = true;
            grabX = mx - KeyShowHud.lastX;
            grabY = my - KeyShowHud.lastY;
            return true;
        }
        return false;
    }

    boolean drag(double mx, double my, int screenW, int screenH) {
        if (!dragging) {
            return false;
        }
        KeyShowConfig cfg = KeyShowConfig.get();
        cfg.x = snap((int) Math.round(mx - grabX), KeyShowHud.lastW, screenW);
        cfg.y = snap((int) Math.round(my - grabY), KeyShowHud.lastH, screenH);
        return true;
    }

    /** Ends the drag; returns whether one was in progress. */
    boolean release() {
        boolean was = dragging;
        dragging = false;
        return was;
    }

    private static int snap(int pos, int size, int screen) {
        if (Math.abs(pos - MARGIN) < SNAP) return MARGIN;
        if (Math.abs(pos + size - (screen - MARGIN)) < SNAP) return screen - MARGIN - size;
        if (Math.abs(pos + size / 2 - screen / 2) < SNAP) return screen / 2 - size / 2;
        return pos;
    }
}
