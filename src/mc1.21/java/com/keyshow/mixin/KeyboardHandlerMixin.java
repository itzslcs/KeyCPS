package com.keyshow.mixin;

import com.keyshow.KeyTracker;
import net.minecraft.client.KeyboardHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Counts presses of keyboard-bound attack/use keys. */
@Mixin(KeyboardHandler.class)
public class KeyboardHandlerMixin {
    @Inject(method = "keyPress", at = @At("HEAD"))
    private void keycps$onKeyPress(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
        KeyTracker.onKeyEvent(key, action);
    }
}
