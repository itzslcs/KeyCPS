package com.keyshow;

import com.keyshow.mixin.KeyMappingAccessor;
import com.mojang.blaze3d.platform.InputConstants;
import java.util.ArrayDeque;
import java.util.List;
import java.util.function.Function;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import org.lwjgl.glfw.GLFW;

/**
 * Tracks which keys are held and how often they fire.
 * Everything is counted from GLFW input events, so no click is lost between frames, and
 * holding a keyboard key counts the OS key repeat too (aaaaaaaa).
 */
public class KeyTracker {
    /** Presses in the last second. */
    public static final class Rate {
        private static final long WINDOW_NS = 1_000_000_000L;
        private final ArrayDeque<Long> times = new ArrayDeque<>();

        void record(long now) {
            times.addLast(now);
            prune(now);
        }

        int count(long now) {
            prune(now);
            return times.size();
        }

        private void prune(long now) {
            while (!times.isEmpty() && now - times.peekFirst() >= WINDOW_NS) {
                times.pollFirst();
            }
        }
    }

    public static final class Tracked {
        private final Function<Options, KeyMapping> mapping;
        final Rate rate = new Rate();
        /** 0 = released, 1 = pressed; eased towards the real state for the fade animation. */
        float glow;

        Tracked(Function<Options, KeyMapping> mapping) {
            this.mapping = mapping;
        }

        KeyMapping mapping() {
            return mapping.apply(Minecraft.getInstance().options);
        }

        public boolean down() {
            return mapping().isDown();
        }

        public int rate() {
            return rate.count(System.nanoTime());
        }

        public String label() {
            return keyLabel(mapping());
        }
    }

    public static final Tracked FORWARD = new Tracked(o -> o.keyUp);
    public static final Tracked LEFT = new Tracked(o -> o.keyLeft);
    public static final Tracked BACK = new Tracked(o -> o.keyDown);
    public static final Tracked RIGHT = new Tracked(o -> o.keyRight);
    public static final Tracked JUMP = new Tracked(o -> o.keyJump);
    public static final Tracked SNEAK = new Tracked(o -> o.keyShift);
    public static final Tracked SPRINT = new Tracked(o -> o.keySprint);
    public static final Tracked ATTACK = new Tracked(o -> o.keyAttack);
    public static final Tracked USE = new Tracked(o -> o.keyUse);
    static final List<Tracked> ALL = List.of(FORWARD, LEFT, BACK, RIGHT, JUMP, SNEAK, SPRINT, ATTACK, USE);

    static InputConstants.Key boundKey(KeyMapping m) {
        return ((KeyMappingAccessor) m).getKey();
    }

    /** Called from KeyboardHandlerMixin for every keyboard event, including repeats. */
    public static void onKeyEvent(int key, int action) {
        Minecraft mc = Minecraft.getInstance();
        // Only count in-game: typing in chat shouldn't bump the counters.
        if (mc.options == null || mc.screen != null || (action != GLFW.GLFW_PRESS && action != GLFW.GLFW_REPEAT)) {
            return;
        }
        count(InputConstants.Type.KEYSYM, key);
    }

    /** Called from MouseHandlerMixin for every mouse button event. */
    public static void onMouseEvent(int button, int action) {
        Minecraft mc = Minecraft.getInstance();
        // Count in-game and in our own screens (so the preview shows your CPS), not in inventories or menus.
        if (mc.options == null || action != GLFW.GLFW_PRESS || mc.screen != null && !(mc.screen instanceof BaseScreen)) {
            return;
        }
        count(InputConstants.Type.MOUSE, button);
    }

    private static void count(InputConstants.Type type, int value) {
        long now = System.nanoTime();
        for (Tracked t : ALL) {
            InputConstants.Key k = boundKey(t.mapping());
            if (k.getType() == type && k.getValue() == value) {
                t.rate.record(now);
            }
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
            case GLFW.GLFW_KEY_UNKNOWN -> "-";
            case GLFW.GLFW_KEY_SPACE -> "Space";
            case GLFW.GLFW_KEY_ENTER -> "Enter";
            case GLFW.GLFW_KEY_TAB -> "Tab";
            case GLFW.GLFW_KEY_CAPS_LOCK -> "Caps";
            case GLFW.GLFW_KEY_LEFT_SHIFT, GLFW.GLFW_KEY_RIGHT_SHIFT -> "Shift";
            case GLFW.GLFW_KEY_LEFT_CONTROL, GLFW.GLFW_KEY_RIGHT_CONTROL -> "Ctrl";
            case GLFW.GLFW_KEY_LEFT_ALT, GLFW.GLFW_KEY_RIGHT_ALT -> "Alt";
            default -> {
                String name = key.getDisplayName().getString();
                yield name.length() > 6 ? name.substring(0, 6) : name.toUpperCase();
            }
        };
    }
}
