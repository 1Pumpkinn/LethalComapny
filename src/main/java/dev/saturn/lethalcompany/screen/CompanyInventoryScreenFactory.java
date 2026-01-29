package dev.saturn.lethalcompany.screen;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class CompanyInventoryScreenFactory implements NamedScreenHandlerFactory {
    public CompanyInventoryScreenFactory(ServerPlayerEntity player) {
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("screen.lethalcompany.company_inventory");
    }

    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new CompanyInventoryScreenHandler(syncId, playerInventory);
    }
}
