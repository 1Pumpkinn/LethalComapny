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
            if (lethalcompany$findTwoHandedSlot() != -1) {
                return;
            }

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

    @Inject(method = "scrollInHotbar", at = @At("HEAD"), cancellable = true)
    public void lethalcompany$scrollInHotbar(double scrollAmount, CallbackInfo ci) {
        if (this.player.isCreative()) {
            return;
        }

        if (lethalcompany$findTwoHandedSlot() != -1) {
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

    @Inject(method = "updateItems", at = @At("HEAD"))
    private void lethalcompany$clampSelectedSlot(CallbackInfo ci) {
        if (!this.player.isCreative()) {
            this.offHand.set(0, ItemStack.EMPTY);

            int firstTwoHandedSlot = lethalcompany$enforceSingleTwoHanded();
            if (firstTwoHandedSlot != -1) {
                this.selectedSlot = firstTwoHandedSlot;
                return;
            }
            
            if (this.selectedSlot >= 4) {
                this.selectedSlot %= 4;
            }
        }
    }

    @Inject(method = "insertStack(Lnet/minecraft/item/ItemStack;)Z", at = @At("HEAD"), cancellable = true)
    public void lethalcompany$insertStack(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (this.player.isCreative()) {
            return;
        }

        // If it's a two-handed item, it goes to vanilla hotbar (hands)
        if (stack.isIn(LethalCompany.TWO_HANDED_ITEMS)) {
            int handSlot = 8;
            if (lethalcompany$hasTwoHandedAnywhere()) {
                cir.setReturnValue(false);
                return;
            }
            
            // If hand slot is occupied by a small item, we can't put a large item there
            // because hand slot (8) is reserved for large items.
            // However, small items should never be in slot 8 anyway due to our redirection.
            if (!this.main.get(handSlot).isEmpty()) {
                cir.setReturnValue(false);
                return;
            }

            this.setStack(handSlot, stack.copy());
            stack.setCount(0);
            this.selectedSlot = handSlot;
            cir.setReturnValue(true);
            return;
        }

        if (lethalcompany$hasTwoHandedAnywhere()) {
            cir.setReturnValue(false);
            return;
        }

        // Redirect small item pickups to CompanyInventory
        Inventory companyInv = ((CompanyInventoryProvider) this.player).lethalcompany$getCompanyInventory();

        boolean insertedAny = false;
        for (int i = 0; i < 4 && !stack.isEmpty(); i++) {
            if (!companyInv.getStack(i).isEmpty()) {
                continue;
            }
            ItemStack single = stack.copy();
            single.setCount(1);
            companyInv.setStack(i, single);
            stack.decrement(1);
            insertedAny = true;
        }

        cir.setReturnValue(insertedAny && stack.isEmpty());
    }

    @Inject(method = "insertStack(ILnet/minecraft/item/ItemStack;)Z", at = @At("HEAD"), cancellable = true)
    public void lethalcompany$insertStackIntoSlot(int slot, ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (this.player.isCreative()) {
            return;
        }

        if (lethalcompany$hasTwoHandedAnywhere()) {
            cir.setReturnValue(false);
            return;
        }

        if (stack.isIn(LethalCompany.TWO_HANDED_ITEMS)) {
            int handSlot = 8;
            if (!this.main.get(handSlot).isEmpty()) {
                cir.setReturnValue(false);
                return;
            }

            this.setStack(handSlot, stack.copy());
            stack.setCount(0);
            this.selectedSlot = handSlot;
            cir.setReturnValue(true);
            return;
        }

        // For small items, redirect to CompanyInventory regardless of requested slot
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

    @Inject(method = "setStack", at = @At("HEAD"))
    public void lethalcompany$onSetStack(int slot, net.minecraft.item.ItemStack stack, CallbackInfo ci) {
        // We could potentially sync vanilla hotbar slots to company inventory here,
        // but it's better to keep them separate for now as requested.
    }

    private int lethalcompany$findTwoHandedSlot() {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = this.main.get(i);
            if (!stack.isEmpty() && stack.isIn(LethalCompany.TWO_HANDED_ITEMS)) {
                return i;
            }
        }
        return -1;
    }

    private boolean lethalcompany$hasTwoHandedAnywhere() {
        for (ItemStack stack : this.main) {
            if (!stack.isEmpty() && stack.isIn(LethalCompany.TWO_HANDED_ITEMS)) {
                return true;
            }
        }
        
        Inventory companyInv = ((CompanyInventoryProvider) this.player).lethalcompany$getCompanyInventory();
        for (int i = 0; i < companyInv.size(); i++) {
            ItemStack stack = companyInv.getStack(i);
            if (!stack.isEmpty() && stack.isIn(LethalCompany.TWO_HANDED_ITEMS)) {
                return true;
            }
        }

        if (this.player.currentScreenHandler != null) {
            ItemStack cursorStack = this.player.currentScreenHandler.getCursorStack();
            if (!cursorStack.isEmpty() && cursorStack.isIn(LethalCompany.TWO_HANDED_ITEMS)) {
                return true;
            }
        }
        
        return false;
    }

    private int lethalcompany$enforceSingleTwoHanded() {
        int keptSlot = -1;
        
        // Check main inventory
        for (int i = 0; i < this.main.size(); i++) {
            ItemStack stack = this.main.get(i);
            if (stack.isEmpty() || !stack.isIn(LethalCompany.TWO_HANDED_ITEMS)) {
                continue;
            }

            if (!this.player.getWorld().isClient) {
                if (i == 8 && keptSlot != 8) {
                    if (keptSlot != -1) {
                        ItemStack toDrop = this.main.get(keptSlot);
                        this.main.set(keptSlot, ItemStack.EMPTY);
                        this.player.dropItem(toDrop, false, true);
                    }
                    keptSlot = 8;
                    continue;
                }

                if (keptSlot == -1) {
                    keptSlot = i;
                    continue;
                }

                this.main.set(i, ItemStack.EMPTY);
                this.player.dropItem(stack, false, true);
            } else {
                if (keptSlot == -1) {
                    keptSlot = i;
                }
            }
        }

        // Check company inventory
        Inventory companyInv = ((CompanyInventoryProvider) this.player).lethalcompany$getCompanyInventory();
        for (int i = 0; i < companyInv.size(); i++) {
            ItemStack stack = companyInv.getStack(i);
            if (stack.isEmpty() || !stack.isIn(LethalCompany.TWO_HANDED_ITEMS)) {
                continue;
            }

            if (!this.player.getWorld().isClient) {
                if (keptSlot == -1) {
                    // This shouldn't happen normally, but if it does, we can't easily move it to slot 8 here
                    // because we might be in the middle of a tick.
                    // Let's just drop it and let the next pickup handle it correctly.
                    ItemStack toDrop = companyInv.removeStack(i);
                    this.player.dropItem(toDrop, false, true);
                } else {
                    ItemStack toDrop = companyInv.removeStack(i);
                    this.player.dropItem(toDrop, false, true);
                }
            } else {
                // On client, we just know we have one
                if (keptSlot == -1) keptSlot = 100 + i; // dummy index
            }
        }

        if (!this.player.getWorld().isClient && keptSlot != -1 && keptSlot != 8) {
            ItemStack handStack = this.main.get(8);
            if (handStack.isEmpty()) {
                this.main.set(8, this.main.get(keptSlot));
                this.main.set(keptSlot, ItemStack.EMPTY);
                keptSlot = 8;
            } else {
                ItemStack toDrop = this.main.get(keptSlot);
                this.main.set(keptSlot, ItemStack.EMPTY);
                this.player.dropItem(toDrop, false, true);
                keptSlot = -1;
            }
        }

        return (keptSlot == 8 || keptSlot >= 100) ? 8 : -1;
    }
}
