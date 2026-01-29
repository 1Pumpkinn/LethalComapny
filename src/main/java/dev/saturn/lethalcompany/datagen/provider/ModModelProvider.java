package dev.saturn.lethalcompany.datagen.provider;

import dev.saturn.lethalcompany.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Models;

public class ModModelProvider extends FabricModelProvider {
    public ModModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        itemModelGenerator.register(ModItems.COMPANY_TABLET, Models.GENERATED);
        itemModelGenerator.register(ModItems.SCRAP_METAL, Models.GENERATED);
        itemModelGenerator.register(ModItems.GOLD_BAR, Models.GENERATED);
        itemModelGenerator.register(ModItems.SOCCER_BALL, Models.GENERATED);
        itemModelGenerator.register(ModItems.TOY_CUBE, Models.GENERATED);
        itemModelGenerator.register(ModItems.V_TYPE_ENGINE, Models.GENERATED);
        itemModelGenerator.register(ModItems.REMOTE, Models.GENERATED);
        itemModelGenerator.register(ModItems.PILL_BOTTLE, Models.GENERATED);
        itemModelGenerator.register(ModItems.PAINTING, Models.GENERATED);
        itemModelGenerator.register(ModItems.LARGE_AXLE, Models.GENERATED);
        itemModelGenerator.register(ModItems.HAIR_BRUSH, Models.GENERATED);
        itemModelGenerator.register(ModItems.CONTROL_PAD, Models.GENERATED);
    }
}
