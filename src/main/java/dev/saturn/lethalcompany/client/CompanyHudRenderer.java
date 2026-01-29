package dev.saturn.lethalcompany.client;

import dev.saturn.lethalcompany.inventory.CompanyInventoryProvider;
import dev.saturn.lethalcompany.registry.ModKeybinds;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

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

    public static ItemEntity lethalcompany$getPickupTarget(PlayerEntity player) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null) {
            return null;
        }

        Vec3d cameraPos = client.gameRenderer.getCamera().getPos();
        Vec3d look = player.getRotationVec(1.0f).normalize();

        double rayRange = 4.5;
        Vec3d rayTarget = cameraPos.add(look.multiply(rayRange));

        EntityHitResult entityHitResult = ProjectileUtil.raycast(
                player,
                cameraPos,
                rayTarget,
                new Box(cameraPos, rayTarget),
                entity -> entity instanceof ItemEntity && !entity.isSpectator() && entity.isAlive(),
                rayRange * rayRange
        );

        if (entityHitResult != null && entityHitResult.getEntity() instanceof ItemEntity hitItem) {
            return hitItem;
        }

        double coneRange = 3.0;
        float pitch = player.getPitch();
        if (pitch < 25.0f) {
            return null;
        }

        Box search = player.getBoundingBox().expand(coneRange);
        java.util.List<ItemEntity> nearby = player.getWorld().getEntitiesByClass(ItemEntity.class, search, entity -> {
            Vec3d target = entity.getBoundingBox().getCenter();
            Vec3d toTarget = target.subtract(cameraPos);
            double dist = toTarget.length();
            if (dist > coneRange || dist < 0.001) {
                return false;
            }
            double dot = look.dotProduct(toTarget.normalize());
            if (dot < 0.70) {
                return false;
            }
            var blockHit = player.getWorld().raycast(new RaycastContext(
                    cameraPos,
                    target,
                    RaycastContext.ShapeType.COLLIDER,
                    RaycastContext.FluidHandling.NONE,
                    player
            ));
            return blockHit.getType() == HitResult.Type.MISS;
        });
        if (nearby.isEmpty()) {
            return null;
        }

        nearby.sort((a, b) -> {
            Vec3d ta = a.getBoundingBox().getCenter().subtract(cameraPos).normalize();
            Vec3d tb = b.getBoundingBox().getCenter().subtract(cameraPos).normalize();
            double dotA = look.dotProduct(ta);
            double dotB = look.dotProduct(tb);
            int cmp = Double.compare(dotB, dotA);
            if (cmp != 0) {
                return cmp;
            }
            return Double.compare(a.squaredDistanceTo(player), b.squaredDistanceTo(player));
        });
        return nearby.getFirst();
    }

    private void renderCompanyHotbar(DrawContext context, PlayerEntity player, float tickDelta) {
        if (player.isCreative()) return;
        int width = context.getScaledWindowWidth();
        int height = context.getScaledWindowHeight();

        Inventory companyInv = ((CompanyInventoryProvider) player).lethalcompany$getCompanyInventory();
        
        ItemStack mainHand = player.getMainHandStack();
        boolean isHoldingLarge = !mainHand.isEmpty() && mainHand.isIn(dev.saturn.lethalcompany.LethalCompany.TWO_HANDED_ITEMS);
        
        int selectedSlot = player.getInventory().selectedSlot % 4;

        int slotSize = 24;
        int spacing = 4;
        int totalWidth = (4 * slotSize) + (3 * spacing);
        int x = width / 2 - totalWidth / 2;
        int y = height - 40;

        if (!isHoldingLarge) {
            ItemEntity closest = lethalcompany$getPickupTarget(player);
            if (closest != null) {
                String keyText = ModKeybinds.PICKUP.getBoundKeyLocalizedText().getString();
                String itemName = closest.getStack().getName().getString();
                String text = "Pickup [" + keyText + "] [" + itemName + "]";
                int tw = MinecraftClient.getInstance().textRenderer.getWidth(text);
                context.drawTextWithShadow(MinecraftClient.getInstance().textRenderer, text, width / 2 - tw / 2, y - 28, 0xFFFFFFFF);
            }
        }
 
        if (isHoldingLarge) {
            String text = "HANDS FULL";
            int textWidth = MinecraftClient.getInstance().textRenderer.getWidth(text);
            context.drawTextWithShadow(MinecraftClient.getInstance().textRenderer, text, width / 2 - textWidth / 2, y - 12, 0xFFFF5555);
        }

        for (int i = 0; i < 4; i++) {
            int slotX = x + i * (slotSize + spacing);
            int slotY = y;

            int bgColor = isHoldingLarge ? 0x44FF0000 : 0x88000000;
            context.fill(slotX, slotY, slotX + slotSize, slotY + slotSize, bgColor);

            int borderColor;
            if (isHoldingLarge) {
                borderColor = 0xFFFF5555;
            } else {
                borderColor = (i == selectedSlot) ? 0xFF5555FF : 0xFF222244;
            }
            drawCustomBorder(context, slotX, slotY, slotX + slotSize, slotY + slotSize, borderColor);

            ItemStack stack = companyInv.getStack(i);
            if (!stack.isEmpty()) {
                context.drawItem(stack, slotX + 4, slotY + 4);
                if (isHoldingLarge) {
                    context.fill(slotX + 2, slotY + 2, slotX + slotSize - 2, slotY + slotSize - 2, 0x44FF0000);
                }
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
