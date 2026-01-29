package dev.saturn.lethalcompany.registry;

import dev.saturn.lethalcompany.LethalCompany;
import dev.saturn.lethalcompany.screen.CompanyInventoryScreenHandler;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandlerType;

public final class ModScreenHandlers {
    public static final ScreenHandlerType<CompanyInventoryScreenHandler> COMPANY_INVENTORY =
            Registry.register(Registries.SCREEN_HANDLER, LethalCompany.id("company_inventory"),
                    new ScreenHandlerType<>(CompanyInventoryScreenHandler::new, FeatureFlags.VANILLA_FEATURES));

    private ModScreenHandlers() {
    }

    public static void register() {
    }
}
