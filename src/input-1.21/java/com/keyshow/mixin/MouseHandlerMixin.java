package com.keyshow.mixin;

import com.keyshow.KeyTracker;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Feeds every mouse button press to the counters, even ones shorter than a frame. */
@Mixin(MouseHandler.class)
public class MouseHandlerMixin {
    @Inject(method = "onPress", at = @At("HEAD"))
    private void keycps$onPress(long window, int button, int action, int modifiers, CallbackInfo ci) {
        KeyTracker.onMouseEvent(button, action);
    }
}
