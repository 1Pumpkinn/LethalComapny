package dev.saturn.lethalcompany.block;

import dev.saturn.lethalcompany.LethalCompany;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public final class ModBlocks {
    public static final Block SCRAP_CRATE = registerBlock("scrap_crate", new Block(Block.Settings.copy(Blocks.IRON_BLOCK)));

    private ModBlocks() {
    }

    public static void register() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(entries -> entries.add(SCRAP_CRATE));
    }

    private static Block registerBlock(String path, Block block) {
        Block registered = Registry.register(Registries.BLOCK, LethalCompany.id(path), block);
        Registry.register(Registries.ITEM, LethalCompany.id(path), new BlockItem(registered, new Item.Settings()));
        return registered;
    }
}
