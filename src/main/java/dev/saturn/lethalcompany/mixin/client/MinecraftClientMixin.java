package dev.saturn.lethalcompany.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
    @Inject(method = "setScreen", at = @At("HEAD"), cancellable = true)
    private void lethalcompany$preventInventoryOpen(Screen screen, CallbackInfo ci) {
        if (screen instanceof InventoryScreen) {
            MinecraftClient client = (MinecraftClient) (Object) this;
            if (client.player != null && !client.player.isCreative()) {
                ci.cancel();
            }
        }
    }
}
