package com.keyshow;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

/** {@link Canvas} on top of this version's GuiGraphics. */
record GuiCanvas(GuiGraphics g, Font font) implements Canvas {
    @Override
    public void fill(int x1, int y1, int x2, int y2, int argb) {
        g.fill(x1, y1, x2, y2, argb);
    }

    @Override
    public void text(String s, int x, int y, int argb, boolean shadow) {
        g.drawString(font, s, x, y, argb, shadow);
    }

    @Override
    public int width(String s) {
        return font.width(s);
    }

    @Override
    public void push(float x, float y, float scale) {
        g.pose().pushMatrix();
        g.pose().translate(x, y);
        g.pose().scale(scale, scale);
    }

    @Override
    public void pop() {
        g.pose().popMatrix();
    }
}
