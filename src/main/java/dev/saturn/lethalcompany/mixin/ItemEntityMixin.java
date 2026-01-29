package dev.saturn.lethalcompany.mixin;

import dev.saturn.lethalcompany.item.ScrapItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin {
    @Inject(method = "tick", at = @At("HEAD"))
    private void lethalcompany$ensureScrapValue(CallbackInfo ci) {
        ItemEntity self = (ItemEntity) (Object) this;
        if (!(self.getWorld() instanceof ServerWorld serverWorld)) {
            return;
        }

        ItemStack stack = self.getStack();
        if (stack.getItem() instanceof ScrapItem scrapItem) {
            scrapItem.ensureValue(serverWorld, stack);
        }
    }

    @Inject(method = "onPlayerCollision", at = @At("HEAD"), cancellable = true)
    private void lethalcompany$requireManualPickup(PlayerEntity player, CallbackInfo ci) {
        ItemEntity self = (ItemEntity) (Object) this;
        ItemStack stack = self.getStack();
        if (stack.getItem() instanceof ScrapItem) {
            ci.cancel();
        }
    }
}
