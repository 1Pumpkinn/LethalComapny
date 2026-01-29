package dev.saturn.lethalcompany.client;

import dev.saturn.lethalcompany.client.screen.CompanyInventoryScreen;
import dev.saturn.lethalcompany.client.CompanyHudRenderer;
import dev.saturn.lethalcompany.item.ScrapItem;
import dev.saturn.lethalcompany.network.ModNetworking;
import dev.saturn.lethalcompany.registry.ModKeybinds;
import dev.saturn.lethalcompany.registry.ModScreenHandlers;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

import java.util.Comparator;
import java.util.List;

public class LethalCompanyClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        CompanyHudRenderer.register();
        HandledScreens.register(ModScreenHandlers.COMPANY_INVENTORY, CompanyInventoryScreen::new);
        ModNetworking.registerClientReceivers();

        KeyBindingHelper.registerKeyBinding(ModKeybinds.OPEN_COMPANY_INVENTORY);
        KeyBindingHelper.registerKeyBinding(ModKeybinds.SCAN);
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (ModKeybinds.OPEN_COMPANY_INVENTORY.wasPressed()) {
                if (client.player != null) {
                    ModNetworking.sendOpenCompanyInventory();
                }
            }
            while (ModKeybinds.SCAN.wasPressed()) {
                lethalcompany$scan(client);
            }
        });
    }

    private static void lethalcompany$scan(MinecraftClient client) {
        if (client.player == null || client.world == null) {
            return;
        }

        final double range = 10.0;
        Vec3d cameraPos = client.gameRenderer.getCamera().getPos();
        Vec3d look = client.player.getRotationVec(1.0f).normalize();

        Box search = client.player.getBoundingBox().expand(range);
        List<ItemEntity> nearby = client.world.getEntitiesByClass(ItemEntity.class, search, entity -> {
            ItemStack stack = entity.getStack();
            if (!(stack.getItem() instanceof ScrapItem)) {
                return false;
            }

            Vec3d target = entity.getBoundingBox().getCenter();
            Vec3d toTarget = target.subtract(cameraPos);
            double dist = toTarget.length();
            if (dist > range || dist < 0.001) {
                return false;
            }

            double dot = look.dotProduct(toTarget.normalize());
            if (dot < 0.35) {
                return false;
            }

            BlockHitResult blockHit = client.world.raycast(new RaycastContext(
                    cameraPos,
                    target,
                    RaycastContext.ShapeType.COLLIDER,
                    RaycastContext.FluidHandling.NONE,
                    client.player
            ));
            return blockHit.getType() == HitResult.Type.MISS;
        });

        if (nearby.isEmpty()) {
            client.player.sendMessage(Text.literal("No scrap detected"), true);
            return;
        }

        nearby.sort(Comparator.comparingDouble(entity -> entity.squaredDistanceTo(client.player)));
        ItemEntity closest = nearby.getFirst();
        ItemStack stack = closest.getStack();

        ScrapItem scrapItem = (ScrapItem) stack.getItem();
        int value = ScrapItem.getValue(stack);
        String valueText = value >= 0 ? ("$" + value) : ("$" + scrapItem.minValue() + "-$" + scrapItem.maxValue());

        MutableText message = Text.literal(stack.getName().getString() + " - Value: " + valueText);
        if (nearby.size() > 1) {
            message = message.append(Text.literal(" (+" + (nearby.size() - 1) + " more)"));
        }
        client.player.sendMessage(message, true);
    }
}
