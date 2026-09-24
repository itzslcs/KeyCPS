package com.keyshow.mixin;

import com.keyshow.KeyTracker;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.input.KeyEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Counts presses of keyboard-bound attack/use keys. */
@Mixin(KeyboardHandler.class)
public class KeyboardHandlerMixin {
    // Since 1.21.9 Minecraft passes the key as a KeyEvent: keyPress(long window, int action, KeyEvent event).
    @Inject(method = "keyPress", at = @At("HEAD"))
    private void keycps$onKeyPress(long window, int action, KeyEvent event, CallbackInfo ci) {
        KeyTracker.onKeyEvent(event.key(), action);
    }
}
