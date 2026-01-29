package dev.saturn.lethalcompany.inventory;

import dev.saturn.lethalcompany.network.ModNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.collection.DefaultedList;

public class CompanyInventory implements Inventory {
    private final DefaultedList<ItemStack> stacks;
    private final PlayerEntity owner;

    public CompanyInventory(DefaultedList<ItemStack> stacks, PlayerEntity owner) {
        this.stacks = stacks;
        this.owner = owner;
    }

    @Override
    public int size() {
        return stacks.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : stacks) {
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getStack(int slot) {
        return stacks.get(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        ItemStack original = stacks.get(slot);
        if (original.isEmpty()) {
            return ItemStack.EMPTY;
        }

        ItemStack result = original.split(amount);
        if (original.isEmpty()) {
            stacks.set(slot, ItemStack.EMPTY);
        }
        markDirty();
        return result;
    }

    @Override
    public ItemStack removeStack(int slot) {
        ItemStack result = stacks.get(slot);
        stacks.set(slot, ItemStack.EMPTY);
        markDirty();
        return result;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        stacks.set(slot, stack);
        if (stack.getCount() > getMaxCountPerStack()) {
            stack.setCount(getMaxCountPerStack());
        }
        markDirty();
    }

    @Override
    public int getMaxCountPerStack() {
        return 1;
    }

    @Override
    public void markDirty() {
        if (this.owner instanceof ServerPlayerEntity serverPlayer) {
            ModNetworking.syncCompanyInventory(serverPlayer);
        }
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return true;
    }

    @Override
    public void clear() {
        for (int i = 0; i < stacks.size(); i++) {
            stacks.set(i, ItemStack.EMPTY);
        }
    }
}
