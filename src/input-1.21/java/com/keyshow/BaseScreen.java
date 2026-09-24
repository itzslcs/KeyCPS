package com.keyshow;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/** Screens that draw the HUD preview and can drag it. Only the Minecraft-specific hooks live here. */
public abstract class BaseScreen extends Screen {
    protected BaseScreen(Component title) {
        super(title);
    }

    /** Drawn over the background, under the widgets. */
    protected abstract void drawOverlay(Canvas c);

    protected boolean dimBackground() {
        return true;
    }

    protected boolean onMousePress(double x, double y) {
        return false;
    }

    protected boolean onMouseDrag(double x, double y) {
        return false;
    }

    protected void onMouseRelease() {
    }

    @Override
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        if (dimBackground() || this.minecraft.level == null) {
            super.renderBackground(graphics, mouseX, mouseY, delta);
        }
        drawOverlay(new GuiCanvas(graphics, this.font));
    }

    @Override
    public boolean mouseClicked(double x, double y, int button) {
        return super.mouseClicked(x, y, button) || button == 0 && onMousePress(x, y);
    }

    @Override
    public boolean mouseDragged(double x, double y, int button, double dx, double dy) {
        return onMouseDrag(x, y) || super.mouseDragged(x, y, button, dx, dy);
    }

    @Override
    public boolean mouseReleased(double x, double y, int button) {
        onMouseRelease();
        return super.mouseReleased(x, y, button);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
