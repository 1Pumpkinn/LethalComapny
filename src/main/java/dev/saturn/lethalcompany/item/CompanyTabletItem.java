package dev.saturn.lethalcompany.item;

import dev.saturn.lethalcompany.screen.CompanyInventoryScreenFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class CompanyTabletItem extends Item {
    public CompanyTabletItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient && user instanceof ServerPlayerEntity serverPlayer) {
            serverPlayer.openHandledScreen(new CompanyInventoryScreenFactory(serverPlayer));
        }
        return TypedActionResult.success(user.getStackInHand(hand), world.isClient);
    }
}
