package dev.saturn.lethalcompany.mixin;

import dev.saturn.lethalcompany.LethalCompany;
import dev.saturn.lethalcompany.inventory.CompanyInventoryProvider;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerInventory.class)
public abstract class PlayerInventoryMixin {
    @Shadow public int selectedSlot;
    @Shadow @Final public PlayerEntity player;
    @Shadow @Final public net.minecraft.util.collection.DefaultedList<ItemStack> main;
    @Shadow @Final public net.minecraft.util.collection.DefaultedList<ItemStack> offHand;
    @Shadow public abstract void setStack(int slot, ItemStack stack);

    @Inject(method = "scrollInHotbar", at = @At("HEAD"), cancellable = true)
    public void lethalcompany$scrollInHotbar(double scrollAmount, CallbackInfo ci) {
        if (this.player.isCreative()) {
            return;
        }

        if (lethalcompany$hasTwoHandedInHotbar()) {
            ci.cancel();
            return;
        }

        int i = (int) Math.signum(scrollAmount);
        this.selectedSlot -= i;
        while (this.selectedSlot < 0) {
            this.selectedSlot += 4;
        }
        while (this.selectedSlot >= 4) {
            this.selectedSlot -= 4;
        }
        ci.cancel();
    }

    @Inject(method = "getMainHandStack", at = @At("HEAD"), cancellable = true)
    private void lethalcompany$getMainHandStack(CallbackInfoReturnable<ItemStack> cir) {
        if (this.player.isCreative()) {
            return;
        }

        ItemStack vanillaSelected = this.main.get(this.selectedSlot);
        if (!vanillaSelected.isEmpty() && vanillaSelected.isIn(LethalCompany.TWO_HANDED_ITEMS)) {
            cir.setReturnValue(vanillaSelected);
            return;
        }

        int slot = this.selectedSlot % 4;
        Inventory companyInv = ((CompanyInventoryProvider) this.player).lethalcompany$getCompanyInventory();
        cir.setReturnValue(companyInv.getStack(slot));
    }

    @Inject(method = "dropSelectedItem", at = @At("HEAD"), cancellable = true)
    public void lethalcompany$dropSelectedItem(boolean all, CallbackInfoReturnable<ItemStack> cir) {
        if (!this.player.isCreative()) {
            // Check if holding a large item in vanilla hand
            ItemStack vanillaStack = this.main.get(this.selectedSlot);
            if (!vanillaStack.isEmpty() && vanillaStack.isIn(LethalCompany.TWO_HANDED_ITEMS)) {
                // Let vanilla handle dropping large item
                return;
            }

            // Otherwise drop from custom inventory
            int slot = this.selectedSlot % 4;
            Inventory companyInv = ((CompanyInventoryProvider) this.player).lethalcompany$getCompanyInventory();
            ItemStack stack = companyInv.getStack(slot);
            if (!stack.isEmpty()) {
                ItemStack dropped = all ? companyInv.removeStack(slot) : companyInv.removeStack(slot, 1);
                cir.setReturnValue(dropped);
            } else {
                cir.setReturnValue(ItemStack.EMPTY);
            }
        }
    }

    @Inject(method = "updateItems", at = @At("HEAD"))
    private void lethalcompany$clampSelectedSlot(CallbackInfo ci) {
        if (this.player.isCreative()) {
            return;
        }

        this.offHand.set(0, ItemStack.EMPTY);

        int twoHandedSlot = -1;
        for (int i = 0; i < 9; i++) {
            if (this.main.get(i).isIn(LethalCompany.TWO_HANDED_ITEMS)) {
                twoHandedSlot = i;
                break;
            }
        }

        if (twoHandedSlot != -1) {
            this.selectedSlot = twoHandedSlot;
        } else {
            if (this.selectedSlot >= 4) {
                this.selectedSlot %= 4;
            }
        }
    }

    @Unique
    private boolean lethalcompany$hasTwoHandedInHotbar() {
        for (int i = 0; i < 9; i++) {
            if (this.main.get(i).isIn(LethalCompany.TWO_HANDED_ITEMS)) {
                return true;
            }
        }
        return false;
    }

    @Inject(method = "insertStack(Lnet/minecraft/item/ItemStack;)Z", at = @At("HEAD"), cancellable = true)
    public void lethalcompany$insertStack(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (this.player.isCreative()) {
            return;
        }

        if (lethalcompany$hasTwoHandedInHotbar()) {
            cir.setReturnValue(false);
            return;
        }

        if (stack.isIn(LethalCompany.TWO_HANDED_ITEMS)) {
            if (lethalcompany$hasTwoHandedInHotbar()) {
                cir.setReturnValue(false);
                return;
            }

            int emptySlot = -1;
            for (int i = 0; i < 9; i++) {
                if (this.main.get(i).isEmpty()) {
                    emptySlot = i;
                    break;
                }
            }

            if (emptySlot != -1) {
                this.setStack(emptySlot, stack.copy());
                stack.setCount(0);
                this.selectedSlot = emptySlot;
                cir.setReturnValue(true);
            } else {
                cir.setReturnValue(false);
            }
            return;
        }

        Inventory companyInv = ((CompanyInventoryProvider) this.player).lethalcompany$getCompanyInventory();
        for (int i = 0; i < 4; i++) {
            if (companyInv.getStack(i).isEmpty()) {
                companyInv.setStack(i, stack.copy());
                stack.setCount(0);
                cir.setReturnValue(true);
                return;
            }
        }

        cir.setReturnValue(false);
    }

    @Inject(method = "insertStack(ILnet/minecraft/item/ItemStack;)Z", at = @At("HEAD"), cancellable = true)
    public void lethalcompany$insertStackIntoSlot(int slot, ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        lethalcompany$insertStack(stack, cir);
    }

    @Inject(method = "setStack", at = @At("HEAD"))
    public void lethalcompany$onSetStack(int slot, net.minecraft.item.ItemStack stack, CallbackInfo ci) {
        // We could potentially sync vanilla hotbar slots to company inventory here,
        // but it's better to keep them separate for now as requested.
    }


}