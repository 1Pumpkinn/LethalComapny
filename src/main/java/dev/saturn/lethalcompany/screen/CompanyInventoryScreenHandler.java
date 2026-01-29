package dev.saturn.lethalcompany.screen;

import dev.saturn.lethalcompany.LethalCompany;
import dev.saturn.lethalcompany.inventory.CompanyInventoryProvider;
import dev.saturn.lethalcompany.registry.ModScreenHandlers;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

public class CompanyInventoryScreenHandler extends ScreenHandler {
    public static final int COMPANY_SLOT_COUNT = 4;

    private final Inventory companyInventory;

    public CompanyInventoryScreenHandler(int syncId, PlayerInventory playerInventory) {
        super(ModScreenHandlers.COMPANY_INVENTORY, syncId);
        this.companyInventory = ((CompanyInventoryProvider) playerInventory.player).lethalcompany$getCompanyInventory();

        for (int i = 0; i < COMPANY_SLOT_COUNT; i++) {
            this.addSlot(new Slot(companyInventory, i, 53 + i * 18, 35) {
                @Override
                public int getMaxItemCount() {
                    return 1;
                }

                @Override
                public boolean canInsert(ItemStack stack) {
                    return !stack.isIn(LethalCompany.TWO_HANDED_ITEMS);
                }
            });
        }

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory, 9 + row * 9 + col, 8 + col * 18, 84 + row * 18));
            }
        }

        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
        }
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return companyInventory.canPlayerUse(player);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slotIndex) {
        ItemStack copied = ItemStack.EMPTY;
        Slot slot = this.slots.get(slotIndex);
        if (slot != null && slot.hasStack()) {
            ItemStack original = slot.getStack();
            copied = original.copy();

            if (slotIndex < COMPANY_SLOT_COUNT) {
                if (!this.insertItem(original, COMPANY_SLOT_COUNT, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (!this.insertItem(original, 0, COMPANY_SLOT_COUNT, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (original.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }

        return copied;
    }
}
