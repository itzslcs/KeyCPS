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
    // NOTE: recovered exactly as published in 1.5+1.21.9 / 1.5+1.21.10.
    // Since 1.21.9 keyPress is keyPress(long window, int action, KeyEvent event),
    // but this handler still has the pre-1.21.9 parameter list.
    @Inject(method = "keyPress(JILnet/minecraft/client/input/KeyEvent;)V", at = @At("HEAD"))
    private void keycps$onKeyPress(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
        KeyTracker.onKeyEvent(key, action);
    }
}
