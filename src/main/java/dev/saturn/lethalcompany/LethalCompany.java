package dev.saturn.lethalcompany;

import dev.saturn.lethalcompany.network.ModNetworking;
import dev.saturn.lethalcompany.block.ModBlocks;
import dev.saturn.lethalcompany.item.ModItems;
import dev.saturn.lethalcompany.registry.ModScreenHandlers;
import net.fabricmc.api.ModInitializer;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LethalCompany implements ModInitializer {
    public static final String MOD_ID = "lethalcompany";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final TagKey<Item> TWO_HANDED_ITEMS = TagKey.of(RegistryKeys.ITEM, id("two_handed"));

    public static Identifier id(String path) {
        return Identifier.of(MOD_ID, path);
    }

    @Override
    public void onInitialize() {
        ModBlocks.register();
        ModItems.register();
        ModScreenHandlers.register();
        ModNetworking.registerPayloads();
        ModNetworking.registerServerReceivers();
    }
}
