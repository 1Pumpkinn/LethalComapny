package dev.saturn.lethalcompany.datagen.provider;

import dev.saturn.lethalcompany.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class ModEnglishLangProvider extends FabricLanguageProvider {
    public ModEnglishLangProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookupFuture) {
        super(dataOutput, registryLookupFuture);
    }

    @Override
    public void generateTranslations(RegistryWrapper.WrapperLookup registryLookup, TranslationBuilder translationBuilder) {
        translationBuilder.add(ModItems.COMPANY_TABLET, "Company Tablet");
        translationBuilder.add(ModItems.SCRAP_METAL, "Scrap Metal");
        translationBuilder.add(ModItems.GOLD_BAR, "Gold Bar");
        translationBuilder.add(ModItems.SOCCER_BALL, "Soccer Ball");
        translationBuilder.add(ModItems.TOY_CUBE, "Toy Cube");
        translationBuilder.add(ModItems.V_TYPE_ENGINE, "V-Type Engine");
        translationBuilder.add(ModItems.REMOTE, "Remote");
        translationBuilder.add(ModItems.PILL_BOTTLE, "Pill Bottle");
        translationBuilder.add(ModItems.PAINTING, "Painting");
        translationBuilder.add(ModItems.LARGE_AXLE, "Large Axle");
        translationBuilder.add(ModItems.HAIR_BRUSH, "Hair Brush");
        translationBuilder.add(ModItems.CONTROL_PAD, "Control Pad");
        translationBuilder.add("screen.lethalcompany.company_inventory", "Company Inventory");
        translationBuilder.add("key.lethalcompany.open_company_inventory", "Open Company Inventory");
        translationBuilder.add("key.lethalcompany.scan", "Scan");
        translationBuilder.add("category.lethalcompany", "Lethal Company");
    }
}
