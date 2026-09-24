package com.keyshow;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
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
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        return super.mouseClicked(event, doubleClick) || event.button() == 0 && onMousePress(event.x(), event.y());
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dx, double dy) {
        return onMouseDrag(event.x(), event.y()) || super.mouseDragged(event, dx, dy);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        onMouseRelease();
        return super.mouseReleased(event);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
