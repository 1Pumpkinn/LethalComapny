package dev.saturn.lethalcompany.client;

import dev.saturn.lethalcompany.inventory.CompanyInventoryProvider;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class CompanyHudRenderer implements HudRenderCallback {
    private static final Identifier SLOT_TEXTURE = Identifier.ofVanilla("textures/gui/container/slot.png");
    private static final Identifier WIDGETS_TEXTURE = Identifier.ofVanilla("textures/gui/widgets.png");

    public static void register() {
        HudRenderCallback.EVENT.register(new CompanyHudRenderer());
    }

    @Override
    public void onHudRender(DrawContext context, RenderTickCounter tickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.options.hudHidden) return;

        renderCompanyHotbar(context, client.player, tickCounter.getTickDelta(true));
    }

    private void renderCompanyHotbar(DrawContext context, PlayerEntity player, float tickDelta) {
        if (player.isCreative()) return;
        int width = context.getScaledWindowWidth();
        int height = context.getScaledWindowHeight();

        Inventory companyInv = ((CompanyInventoryProvider) player).lethalcompany$getCompanyInventory();
        int selectedSlot = player.getInventory().selectedSlot % 4;

        int slotSize = 24;
        int spacing = 4;
        int totalWidth = (4 * slotSize) + (3 * spacing);
        int x = width / 2 - totalWidth / 2;
        int y = height - 40;

        for (int i = 0; i < 4; i++) {
            int slotX = x + i * (slotSize + spacing);
            int slotY = y;

            // Draw dark background (Lethal Company style)
            context.fill(slotX, slotY, slotX + slotSize, slotY + slotSize, 0x88000000);

            // Draw border
            int borderColor = (i == selectedSlot) ? 0xFF5555FF : 0xFF222244;
            drawCustomBorder(context, slotX, slotY, slotX + slotSize, slotY + slotSize, borderColor);

            ItemStack stack = companyInv.getStack(i);
            if (!stack.isEmpty()) {
                context.drawItem(stack, slotX + 4, slotY + 4);
            }
        }
    }

    private void drawCustomBorder(DrawContext context, int x1, int y1, int x2, int y2, int color) {
        context.fill(x1, y1, x2, y1 + 1, color); // Top
        context.fill(x1, y2 - 1, x2, y2, color); // Bottom
        context.fill(x1, y1, x1 + 1, y2, color); // Left
        context.fill(x2 - 1, y1, x2, y2, color); // Right
    }
}
