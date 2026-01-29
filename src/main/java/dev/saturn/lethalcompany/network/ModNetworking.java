package dev.saturn.lethalcompany.network;

import dev.saturn.lethalcompany.inventory.CompanyInventoryProvider;
import dev.saturn.lethalcompany.screen.CompanyInventoryScreenFactory;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.inventory.Inventory;
import net.minecraft.server.network.ServerPlayerEntity;

public final class ModNetworking {
    private ModNetworking() {
    }

    public static void registerPayloads() {
        PayloadTypeRegistry.playC2S().register(OpenCompanyInventoryPayload.ID, OpenCompanyInventoryPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(CompanyInventorySyncPayload.ID, CompanyInventorySyncPayload.CODEC);
    }

    public static void registerServerReceivers() {
        ServerPlayNetworking.registerGlobalReceiver(OpenCompanyInventoryPayload.ID, (payload, context) -> {
            context.player().server.execute(() -> openCompanyInventory(context.player()));
        });

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayerEntity player = handler.player;
            server.execute(() -> syncCompanyInventory(player));
        });
    }

    public static void sendOpenCompanyInventory() {
        ClientPlayNetworking.send(new OpenCompanyInventoryPayload());
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
}
