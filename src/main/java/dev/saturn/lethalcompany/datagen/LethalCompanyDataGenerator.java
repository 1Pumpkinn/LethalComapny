package dev.saturn.lethalcompany.datagen;

import dev.saturn.lethalcompany.datagen.provider.ModEnglishLangProvider;
import dev.saturn.lethalcompany.datagen.provider.ModModelProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class LethalCompanyDataGenerator implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        pack.addProvider(ModEnglishLangProvider::new);
        pack.addProvider(ModModelProvider::new);
    }
}
