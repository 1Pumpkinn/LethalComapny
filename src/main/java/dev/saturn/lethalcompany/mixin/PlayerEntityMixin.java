package dev.saturn.lethalcompany.mixin;

import dev.saturn.lethalcompany.LethalCompany;
import dev.saturn.lethalcompany.inventory.CompanyInventory;
import dev.saturn.lethalcompany.inventory.CompanyInventoryProvider;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements CompanyInventoryProvider {
    protected PlayerEntityMixin(net.minecraft.entity.EntityType<? extends LivingEntity> entityType, net.minecraft.world.World world) {
        super(entityType, world);
    }

    @Unique
    private static final String LETHALCOMPANY_COMPANY_INV_KEY = "LethalCompanyCompanyInventory";

    @Unique
    private static final Identifier TWO_HANDED_SPEED_PENALTY_ID = LethalCompany.id("two_handed_speed_penalty");

    @Unique
    private final DefaultedList<ItemStack> lethalcompany$companyStacks = DefaultedList.ofSize(4, ItemStack.EMPTY);

    @Unique
    private final Inventory lethalcompany$companyInventory = new CompanyInventory(lethalcompany$companyStacks, (PlayerEntity) (Object) this);

    @Override
    public Inventory lethalcompany$getCompanyInventory() {
        return lethalcompany$companyInventory;
    }

    @Inject(method = "getEquippedStack", at = @At("HEAD"), cancellable = true)
    private void lethalcompany$getEquippedStack(EquipmentSlot slot, CallbackInfoReturnable<ItemStack> cir) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        if (slot == EquipmentSlot.MAINHAND) {
            if (!player.isCreative()) {
                // If holding a large object in vanilla main hand, return it
                ItemStack vanillaStack = player.getInventory().getMainHandStack();
                if (!vanillaStack.isEmpty() && vanillaStack.isIn(LethalCompany.TWO_HANDED_ITEMS)) {
                    cir.setReturnValue(vanillaStack);
                    return;
                }
                
                // Otherwise return from custom inventory
                int selectedSlot = player.getInventory().selectedSlot % 4;
                cir.setReturnValue(this.lethalcompany$companyInventory.getStack(selectedSlot));
            }
        } else if (slot == EquipmentSlot.OFFHAND) {
            if (!player.isCreative()) {
                cir.setReturnValue(ItemStack.EMPTY);
            }
        }
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void lethalcompany$tickLargeItemPenalties(CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        if (player.getWorld().isClient) return;

        ItemStack mainHand = player.getMainHandStack();
        boolean isHoldingLarge = !mainHand.isEmpty() && mainHand.isIn(LethalCompany.TWO_HANDED_ITEMS);

        var moveSpeed = player.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
        if (moveSpeed != null) {
            if (isHoldingLarge) {
                if (moveSpeed.getModifier(TWO_HANDED_SPEED_PENALTY_ID) == null) {
                    moveSpeed.addTemporaryModifier(new EntityAttributeModifier(TWO_HANDED_SPEED_PENALTY_ID, -0.2, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
                }
            } else {
                moveSpeed.removeModifier(TWO_HANDED_SPEED_PENALTY_ID);
            }
        }
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    private void lethalcompany$writeCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {
        NbtCompound companyInvNbt = new NbtCompound();
        Inventories.writeNbt(companyInvNbt, lethalcompany$companyStacks, ((PlayerEntity) (Object) this).getRegistryManager());
        nbt.put(LETHALCOMPANY_COMPANY_INV_KEY, companyInvNbt);
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    private void lethalcompany$readCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
        if (!nbt.contains(LETHALCOMPANY_COMPANY_INV_KEY)) {
            return;
        }
        NbtCompound companyInvNbt = nbt.getCompound(LETHALCOMPANY_COMPANY_INV_KEY);
        Inventories.readNbt(companyInvNbt, lethalcompany$companyStacks, ((PlayerEntity) (Object) this).getRegistryManager());
    }
}
