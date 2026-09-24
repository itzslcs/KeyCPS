package com.keyshow;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;

/** In-game settings (also opened from Mod Menu). The HUD preview is drawn live behind the buttons. */
public class ConfigScreen extends BaseScreen {
    private static final int[] PALETTE = {
            0xFFFFFF, 0x000000, 0x808080, 0xFF5555, 0xFFAA00, 0xFFFF55,
            0x55FF55, 0x00AA00, 0x55FFFF, 0x5555FF, 0xAA00AA, 0xFF55FF};
    private static final String[] PALETTE_NAMES = {
            "white", "black", "gray", "red", "orange", "yellow",
            "lime", "green", "aqua", "blue", "purple", "pink"};
    private static final int BW = 150;
    private static final int BH = 20;
    private static final int GAP = 4;

    private final Screen parent;
    private int titleY;

    public ConfigScreen(Screen parent) {
        super(Component.translatable("keycps.config.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        KeyShowConfig cfg = KeyShowConfig.get();
        List<AbstractWidget> w = new ArrayList<>();
        w.add(toggle("enabled", cfg.enabled, v -> cfg.enabled = v));
        w.add(new Slider("scale", 0.5, 3.0, 0.05, cfg.scale, v -> cfg.scale = (float) v,
                v -> Component.literal(Math.round(v * 100) + "%")));
        w.add(toggle("movement", cfg.showMovement, v -> cfg.showMovement = v));
        w.add(toggle("jump", cfg.showJump, v -> cfg.showJump = v));
        w.add(toggle("mouse", cfg.showMouse, v -> cfg.showMouse = v));
        w.add(toggle("cps", cfg.showCps, v -> cfg.showCps = v));
        w.add(toggle("mouse_labels", cfg.alwaysMouseLabels, v -> cfg.alwaysMouseLabels = v));
        w.add(toggle("sneak_sprint", cfg.showSneakSprint, v -> cfg.showSneakSprint = v));
        w.add(toggle("key_rates", cfg.showKeyRates, v -> cfg.showKeyRates = v));
        w.add(new Slider("cps_warn", 0, 30, 1, cfg.cpsWarn, v -> cfg.cpsWarn = (int) v,
                v -> v < 1 ? Component.translatable("options.off") : Component.literal(String.valueOf((int) v))));
        w.add(toggle("background", cfg.showBg, v -> cfg.showBg = v));
        w.add(new Slider("bg_opacity", 0, 255, 1, cfg.bgAlpha, v -> cfg.bgAlpha = (int) v,
                v -> Component.literal(Math.round(v / 2.55) + "%")));
        w.add(color("bg_color", () -> cfg.bgColor, v -> cfg.bgColor = v));
        w.add(color("text_color", () -> cfg.textColor, v -> cfg.textColor = v));
        w.add(color("pressed_color", () -> cfg.pressedFill, v -> cfg.pressedFill = v));
        w.add(toggle("rounded", cfg.rounded, v -> cfg.rounded = v));
        w.add(toggle("shadow", cfg.textShadow, v -> cfg.textShadow = v));
        w.add(toggle("fade", cfg.fade, v -> cfg.fade = v));
        w.add(toggle("rainbow", cfg.rainbow, v -> cfg.rainbow = v));

        // Use the fewest columns (2-4) that fit the screen height, and shrink buttons to fit the width.
        int cols = 2;
        while (cols < 4 && 28 + (w.size() + cols - 1) / cols * (BH + GAP) + BH + 10 > this.height) {
            cols++;
        }
        int bw = Math.min(BW, (this.width - 16 - (cols - 1) * GAP) / cols);
        int rows = (w.size() + cols - 1) / cols;
        int gridH = rows * (BH + GAP) + BH + 6;
        int top = Math.max(24, (this.height - gridH) / 2 + 8);
        titleY = Math.max(4, top - 15);
        int left = (this.width - (cols * bw + (cols - 1) * GAP)) / 2;
        for (int i = 0; i < w.size(); i++) {
            AbstractWidget widget = w.get(i);
            widget.setWidth(bw);
            widget.setX(left + i % cols * (bw + GAP));
            widget.setY(top + i / cols * (BH + GAP));
            addRenderableWidget(widget);
        }

        int by = Math.min(this.height - BH - 4, top + rows * (BH + GAP) + 4);
        int fw = Math.min(100, (this.width - 16 - 2 * GAP) / 3);
        int fx = (this.width - (3 * fw + 2 * GAP)) / 2;
        addRenderableWidget(Button.builder(Component.translatable("keycps.config.move"),
                b -> Platform.setScreen(this.minecraft, new MoveScreen(this)))
                .bounds(fx, by, fw, BH).tooltip(Tooltip.create(Component.translatable("keycps.config.move.tooltip"))).build());
        addRenderableWidget(Button.builder(Component.translatable("keycps.config.reset"), b -> {
            KeyShowConfig.reset();
            Platform.setScreen(this.minecraft, new ConfigScreen(this.parent));
        }).bounds(fx + fw + GAP, by, fw, BH).build());
        addRenderableWidget(Button.builder(Component.translatable("gui.done"), b -> onClose())
                .bounds(fx + 2 * (fw + GAP), by, fw, BH).build());
    }

    @Override
    protected void drawOverlay(Canvas c) {
        KeyShowHud.draw(c, this.width, this.height);
        c.centered(this.title.getString(), this.width / 2, titleY, 0xFFFFFFFF, true);
    }

    @Override
    public void onClose() {
        KeyShowConfig.save();
        Platform.setScreen(this.minecraft, this.parent);
    }

    private static MutableComponent name(String id) {
        return Component.translatable("keycps.config." + id);
    }

    private static Button toggle(String id, boolean initial, Consumer<Boolean> set) {
        boolean[] value = {initial};
        Button b = Button.builder(toggleText(id, initial), btn -> {
            value[0] = !value[0];
            set.accept(value[0]);
            btn.setMessage(toggleText(id, value[0]));
        }).size(BW, BH).build();
        b.setTooltip(Tooltip.create(Component.translatable("keycps.config." + id + ".tooltip")));
        return b;
    }

    private static Component toggleText(String id, boolean on) {
        return name(id).append(": ").append(on
                ? Component.translatable("options.on").withStyle(ChatFormatting.GREEN)
                : Component.translatable("options.off").withStyle(ChatFormatting.RED));
    }

    private static Button color(String id, IntSupplier get, IntConsumer set) {
        Button b = Button.builder(colorText(id, get.getAsInt()), btn -> {
            int i = indexOf(get.getAsInt());
            int next = PALETTE[(i + 1) % PALETTE.length];
            set.accept(next);
            btn.setMessage(colorText(id, next));
        }).size(BW, BH).build();
        b.setTooltip(Tooltip.create(Component.translatable("keycps.config.color.tooltip")));
        return b;
    }

    private static Component colorText(String id, int rgb) {
        int i = indexOf(rgb);
        Component value = i < 0
                ? Component.literal(String.format("#%06X", rgb & 0xFFFFFF))
                : Component.translatable("keycps.color." + PALETTE_NAMES[i]);
        return name(id).append(": ").append(Component.literal("■ ").withColor(rgb & 0xFFFFFF)).append(value);
    }

    private static int indexOf(int rgb) {
        for (int i = 0; i < PALETTE.length; i++) {
            if (PALETTE[i] == (rgb & 0xFFFFFF)) return i;
        }
        return -1;
    }

    private static final class Slider extends AbstractSliderButton {
        private final String id;
        private final double min;
        private final double max;
        private final double step;
        private final java.util.function.DoubleConsumer set;
        private final java.util.function.DoubleFunction<Component> text;

        Slider(String id, double min, double max, double step, double initial,
               java.util.function.DoubleConsumer set, java.util.function.DoubleFunction<Component> text) {
            super(0, 0, BW, BH, Component.empty(), (Mth.clamp(initial, min, max) - min) / (max - min));
            this.id = id;
            this.min = min;
            this.max = max;
            this.step = step;
            this.set = set;
            this.text = text;
            setTooltip(Tooltip.create(Component.translatable("keycps.config." + id + ".tooltip")));
            updateMessage();
        }

        private double real() {
            return Math.round((min + value * (max - min)) / step) * step;
        }

        @Override
        protected void updateMessage() {
            setMessage(name(id).append(": ").append(text.apply(real())));
        }

        @Override
        protected void applyValue() {
            set.accept(real());
        }
    }
}
