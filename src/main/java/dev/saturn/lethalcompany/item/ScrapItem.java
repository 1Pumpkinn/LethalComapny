package dev.saturn.lethalcompany.item;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

public class ScrapItem extends Item {
    public static final String SCRAP_VALUE_KEY = "lethalcompany_scrap_value";

    private final int minValue;
    private final int maxValue;

    public ScrapItem(int minValue, int maxValue, Settings settings) {
        super(settings);
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    public int minValue() {
        return minValue;
    }

    public int maxValue() {
        return maxValue;
    }

    public static boolean hasValue(ItemStack stack) {
        NbtComponent custom = stack.get(DataComponentTypes.CUSTOM_DATA);
        return custom != null && custom.contains(SCRAP_VALUE_KEY);
    }

    public static int getValue(ItemStack stack) {
        NbtComponent custom = stack.get(DataComponentTypes.CUSTOM_DATA);
        if (custom == null || !custom.contains(SCRAP_VALUE_KEY)) {
            return -1;
        }
        return custom.getNbt().getInt(SCRAP_VALUE_KEY);
    }

    public void ensureValue(ServerWorld world, ItemStack stack) {
        if (hasValue(stack)) {
            return;
        }
        int value = this.minValue + world.random.nextInt((this.maxValue - this.minValue) + 1);
        NbtComponent.set(DataComponentTypes.CUSTOM_DATA, stack, nbt -> nbt.putInt(SCRAP_VALUE_KEY, value));
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, net.minecraft.entity.Entity entity, int slot, boolean selected) {
        if (world instanceof ServerWorld serverWorld) {
            ensureValue(serverWorld, stack);
        }
        super.inventoryTick(stack, world, entity, slot, selected);
    }
}

