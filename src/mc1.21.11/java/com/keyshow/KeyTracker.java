package com.keyshow;

import com.keyshow.mixin.KeyMappingAccessor;
import com.mojang.blaze3d.platform.InputConstants;
import java.util.ArrayDeque;
import java.util.Deque;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import org.lwjgl.glfw.GLFW;

/**
 * Tracks which keys are held and counts clicks per second.
 * Attack/use are polled every frame, whether they are bound to a mouse
 * button or a keyboard key.
 */
public class KeyTracker {
    public static boolean forward;
    public static boolean back;
    public static boolean left;
    public static boolean right;
    public static boolean jumping;
    public static boolean attacking;
    public static boolean using;

    private static boolean prevAttackMouseDown;
    private static boolean prevUseMouseDown;
    private static boolean prevAttackKeyDown;
    private static boolean prevUseKeyDown;

    private static final long WINDOW_MS = 1000L;
    private static final Deque<Long> attackTs = new ArrayDeque<>();
    private static final Deque<Long> useTs = new ArrayDeque<>();

    private static InputConstants.Key boundKey(KeyMapping m) {
        return ((KeyMappingAccessor) m).getKey();
    }

    /** The GLFW window handle (Window.handle() in this Minecraft version). */
    public static long windowHandle(Minecraft mc) {
        return mc.getWindow().handle();
    }

    /** Called every frame from the HUD renderer. */
    public static void pollMouse(long windowHandle) {
        Options o = Minecraft.getInstance().options;
        if (o == null) {
            return;
        }

        InputConstants.Key atk = boundKey(o.keyAttack);
        if (atk.getType() == InputConstants.Type.MOUSE) {
            boolean down = GLFW.glfwGetMouseButton(windowHandle, atk.getValue()) == GLFW.GLFW_PRESS;
            if (down && !prevAttackMouseDown) {
                record(attackTs);
            }
            prevAttackMouseDown = down;
        } else {
            prevAttackMouseDown = false;
        }

        InputConstants.Key use = boundKey(o.keyUse);
        if (use.getType() == InputConstants.Type.MOUSE) {
            boolean down = GLFW.glfwGetMouseButton(windowHandle, use.getValue()) == GLFW.GLFW_PRESS;
            if (down && !prevUseMouseDown) {
                record(useTs);
            }
            prevUseMouseDown = down;
        } else {
            prevUseMouseDown = false;
        }

        if (atk.getType() != InputConstants.Type.MOUSE) {
            boolean d = o.keyAttack.isDown();
            if (d && !prevAttackKeyDown) {
                record(attackTs);
            }
            prevAttackKeyDown = d;
        } else {
            prevAttackKeyDown = false;
        }

        if (use.getType() != InputConstants.Type.MOUSE) {
            boolean d = o.keyUse.isDown();
            if (d && !prevUseKeyDown) {
                record(useTs);
            }
            prevUseKeyDown = d;
        } else {
            prevUseKeyDown = false;
        }
    }

    /** Called every client tick. */
    public static void tick() {
        Options o = Minecraft.getInstance().options;
        if (o != null) {
            forward = o.keyUp.isDown();
            back = o.keyDown.isDown();
            left = o.keyLeft.isDown();
            right = o.keyRight.isDown();
            jumping = o.keyJump.isDown();
            attacking = o.keyAttack.isDown();
            using = o.keyUse.isDown();
        }
    }

    public static int getAttackCps() {
        return countInWindow(attackTs);
    }

    public static int getUseCps() {
        return countInWindow(useTs);
    }

    public static String getAttackLabel() {
        Options o = Minecraft.getInstance().options;
        return o == null ? "LMB" : keyLabel(o.keyAttack);
    }

    public static String getUseLabel() {
        Options o = Minecraft.getInstance().options;
        return o == null ? "RMB" : keyLabel(o.keyUse);
    }

    private static void record(Deque<Long> q) {
        long now = System.currentTimeMillis();
        q.addLast(now);
        prune(q, now);
    }

    private static int countInWindow(Deque<Long> q) {
        prune(q, System.currentTimeMillis());
        return q.size();
    }

    private static void prune(Deque<Long> q, long now) {
        while (!q.isEmpty() && now - q.peekFirst() > WINDOW_MS) {
            q.pollFirst();
        }
    }

    static String keyLabel(KeyMapping binding) {
        InputConstants.Key key = boundKey(binding);
        if (key.getType() == InputConstants.Type.MOUSE) {
            return switch (key.getValue()) {
                case 0 -> "LMB";
                case 1 -> "RMB";
                case 2 -> "MMB";
                default -> "M" + (key.getValue() + 1);
            };
        }
        return switch (key.getValue()) {
            case GLFW.GLFW_KEY_SPACE -> "Space";
            case GLFW.GLFW_KEY_ENTER -> "Enter";
            case GLFW.GLFW_KEY_TAB -> "Tab";
            case GLFW.GLFW_KEY_LEFT_SHIFT -> "L Shift";
            case GLFW.GLFW_KEY_LEFT_CONTROL -> "L Ctrl";
            case GLFW.GLFW_KEY_LEFT_ALT -> "L Alt";
            case GLFW.GLFW_KEY_RIGHT_SHIFT -> "R Shift";
            case GLFW.GLFW_KEY_RIGHT_CONTROL -> "R Ctrl";
            default -> key.getDisplayName().getString().toUpperCase();
        };
    }
}
