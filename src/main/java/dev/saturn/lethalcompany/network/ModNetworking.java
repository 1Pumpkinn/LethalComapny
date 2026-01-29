package dev.saturn.lethalcompany.network;

import dev.saturn.lethalcompany.inventory.CompanyInventoryProvider;
import dev.saturn.lethalcompany.screen.CompanyInventoryScreenFactory;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.inventory.Inventory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.text.Text;
import dev.saturn.lethalcompany.LethalCompany;

public final class ModNetworking {
    private ModNetworking() {
    }

    public static void registerPayloads() {
        PayloadTypeRegistry.playC2S().register(OpenCompanyInventoryPayload.ID, OpenCompanyInventoryPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(PickupItemPayload.ID, PickupItemPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(CompanyInventorySyncPayload.ID, CompanyInventorySyncPayload.CODEC);
    }

    public static void registerServerReceivers() {
        ServerPlayNetworking.registerGlobalReceiver(OpenCompanyInventoryPayload.ID, (payload, context) -> {
            context.player().server.execute(() -> openCompanyInventory(context.player()));
        });
        ServerPlayNetworking.registerGlobalReceiver(PickupItemPayload.ID, (payload, context) -> {
            handlePickupRequest(context.player(), payload.entityUuid());
        });

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayerEntity player = handler.player;
            server.execute(() -> syncCompanyInventory(player));
        });
    }

    public static void sendOpenCompanyInventory() {
        ClientPlayNetworking.send(new OpenCompanyInventoryPayload());
    }
    public static void sendPickupItem(java.util.UUID entityUuid) {
        ClientPlayNetworking.send(new PickupItemPayload(entityUuid));
    }

    public static void registerClientReceivers() {
        ClientPlayNetworking.registerGlobalReceiver(CompanyInventorySyncPayload.ID, (payload, context) -> {
            context.client().execute(() -> {
                if (context.client().player == null) {
                    return;
                }
                Inventory companyInv = ((CompanyInventoryProvider) context.client().player).lethalcompany$getCompanyInventory();
                companyInv.setStack(0, payload.slot0());
                companyInv.setStack(1, payload.slot1());
                companyInv.setStack(2, payload.slot2());
                companyInv.setStack(3, payload.slot3());
            });
        });
    }

    public static void syncCompanyInventory(ServerPlayerEntity player) {
        Inventory companyInv = ((CompanyInventoryProvider) player).lethalcompany$getCompanyInventory();
        ServerPlayNetworking.send(
                player,
                new CompanyInventorySyncPayload(
                        companyInv.getStack(0).copy(),
                        companyInv.getStack(1).copy(),
                        companyInv.getStack(2).copy(),
                        companyInv.getStack(3).copy()
                )
        );
    }

    private static void openCompanyInventory(ServerPlayerEntity player) {
        player.openHandledScreen(new CompanyInventoryScreenFactory(player));
    }

    private static void handlePickupRequest(ServerPlayerEntity player, java.util.UUID entityUuid) {
        player.server.execute(() -> {
            ServerWorld world = player.getServerWorld();
            Entity entity = world.getEntity(entityUuid);

            if (!(entity instanceof ItemEntity)) {
                return;
            }

            ItemEntity itemEntity = (ItemEntity) entity;
            ItemStack stack = itemEntity.getStack();

            if (hasTwoHandedInHotbar(player)) {
                player.sendMessage(Text.literal("Drop large item to pick up items").formatted(net.minecraft.util.Formatting.RED), true);
                return;
            }

            if (stack.isIn(LethalCompany.TWO_HANDED_ITEMS)) {
                player.getInventory().insertStack(stack);
            } else {
                Inventory companyInv = ((CompanyInventoryProvider) player).lethalcompany$getCompanyInventory();
                for (int i = 0; i < companyInv.size(); i++) {
                    if (companyInv.getStack(i).isEmpty()) {
                        companyInv.setStack(i, stack.copy());
                        stack.setCount(0);
                        syncCompanyInventory(player);
                        break;
                    }
                }

                if (!stack.isEmpty()) {
                    player.sendMessage(Text.literal("Company inventory is full").formatted(net.minecraft.util.Formatting.RED), true);
                }
            }
            itemEntity.discard();
        });
    }

    private static boolean hasTwoHandedInHotbar(ServerPlayerEntity player) {
        PlayerInventory inv = player.getInventory();
        for (int i = 0; i < 9; i++) {
            if (inv.getStack(i).isIn(LethalCompany.TWO_HANDED_ITEMS)) {
                return true;
            }
        }
        return false;
    }
}
