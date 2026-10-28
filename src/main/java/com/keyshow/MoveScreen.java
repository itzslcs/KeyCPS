package com.keyshow;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/** Drag the HUD with the mouse; it snaps to the screen edges and centre. Esc saves. */
public class MoveScreen extends BaseScreen {
    private final Screen parent;
    private final HudDrag drag = new HudDrag();

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
        int col = drag.dragging() ? 0xFFFFFF55 : 0xFF55FF55;
        c.fill(x1, y1, x2, y1 + 1, col);
        c.fill(x1, y2 - 1, x2, y2, col);
        c.fill(x1, y1, x1 + 1, y2, col);
        c.fill(x2 - 1, y1, x2, y2, col);
        c.centered(Component.translatable("keycps.move.hint").getString(), this.width / 2, this.height / 2 - 4, 0xFFFFFFFF, true);
    }

    @Override
    protected boolean onMousePress(double mx, double my) {
        return drag.press(mx, my);
    }

    @Override
    protected boolean onMouseDrag(double mx, double my) {
        return drag.drag(mx, my, this.width, this.height);
    }

    @Override
    protected void onMouseRelease() {
        drag.release();
    }

    @Override
    public void onClose() {
        KeyShowConfig.save();
        Platform.setScreen(this.minecraft, this.parent);
    }
}
