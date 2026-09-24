package com.keyshow;

/** The few drawing calls the HUD needs; each Minecraft version adapts its own GUI renderer to this. */
public interface Canvas {
    void fill(int x1, int y1, int x2, int y2, int argb);

    void text(String s, int x, int y, int argb, boolean shadow);

    int width(String s);

    /** Moves the origin to (x, y) and scales everything drawn until {@link #pop()}. */
    void push(float x, float y, float scale);

    void pop();

    default void centered(String s, int cx, int y, int argb, boolean shadow) {
        text(s, cx - width(s) / 2, y, argb, shadow);
    }
}
