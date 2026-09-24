package com.keyshow;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

/** Screen opened by the "Move HUD" key: drag with the mouse, Esc to save. */
public class DragScreen extends Screen {
    private boolean dragging;

    public DragScreen() {
        super(Component.literal("Move KeyCPS HUD"));
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        super.render(graphics, mouseX, mouseY, delta);
        graphics.drawCenteredString(this.font, "Drag to move the HUD — Esc to save", this.width / 2, 20, 0xFFFFFFFF);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dragX, double dragY) {
        KeyShowConfig cfg = KeyShowConfig.get();
        cfg.offsetX += (int) Math.round(dragX);
        cfg.offsetY += (int) Math.round(dragY);
        this.dragging = true;
        return true;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void removed() {
        KeyShowHud.moving = false;
        KeyShowConfig.save();
    }
}
