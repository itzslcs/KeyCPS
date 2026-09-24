package com.keyshow.mixin;

import com.keyshow.KeyTracker;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.input.KeyEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Feeds every key press and key repeat to the counters. */
@Mixin(KeyboardHandler.class)
public class KeyboardHandlerMixin {
    @Inject(method = "keyPress", at = @At("HEAD"))
    private void keycps$onKeyPress(long window, int action, KeyEvent event, CallbackInfo ci) {
        KeyTracker.onKeyEvent(event.key(), action);
    }
}
