package dev.saturn.lethalcomapny.item;

import dev.saturn.lethalcomapny.LethalCompany;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {


    // Items go here



    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(LethalCompany.MOD_ID, name), item);
    }


    public static void registerModItems() {
        LethalCompany.LOGGER.info("Registering ModItems for " + LethalCompany.MOD_ID);
    }
}
