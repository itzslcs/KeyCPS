package com.keyshow.mixin;

import com.keyshow.KeyTracker;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.input.MouseButtonInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Feeds every mouse button press to the counters, even ones shorter than a frame. */
@Mixin(MouseHandler.class)
public class MouseHandlerMixin {
    @Inject(method = "onButton", at = @At("HEAD"))
    private void keycps$onButton(long window, MouseButtonInfo info, int action, CallbackInfo ci) {
        KeyTracker.onMouseEvent(info.button(), action);
    }
}
