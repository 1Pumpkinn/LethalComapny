package dev.saturn.lethalcompany.client.screen;

import dev.saturn.lethalcompany.screen.CompanyInventoryScreenHandler;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class CompanyInventoryScreen extends HandledScreen<CompanyInventoryScreenHandler> {
    private static final Identifier BACKGROUND_TEXTURE = Identifier.ofVanilla("textures/gui/container/generic_54.png");

    public CompanyInventoryScreen(CompanyInventoryScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundWidth = 176;
        this.backgroundHeight = 166;
    }

    @Override
    protected void init() {
        super.init();
        this.titleX = (this.backgroundWidth - this.textRenderer.getWidth(this.title)) / 2;
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        
        // Draw main background (top part)
        context.drawTexture(BACKGROUND_TEXTURE, x, y, 0, 0, backgroundWidth, 71); 
        
        // Draw the player inventory part (at the bottom)
        context.drawTexture(BACKGROUND_TEXTURE, x, y + 71, 0, 126, backgroundWidth, 95); 
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }
}
