package dev.saturn.lethalcompany.item;

import dev.saturn.lethalcompany.LethalCompany;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public final class ModItems {
    public static final Item COMPANY_TABLET = register("company_tablet", new CompanyTabletItem(new Item.Settings().maxCount(1)));
    public static final Item SCRAP_METAL = register("scrap_metal", new Item(new Item.Settings()));
    public static final Item GOLD_BAR = register("gold_bar", new ScrapItem(102, 210, new Item.Settings().maxCount(1)));
    public static final Item SOCCER_BALL = register("soccer_ball", new ScrapItem(40, 70, new Item.Settings().maxCount(1)));
    public static final Item TOY_CUBE = register("toy_cube", new ScrapItem(24, 44, new Item.Settings().maxCount(1)));
    public static final Item V_TYPE_ENGINE = register("v_type_engine", new ScrapItem(20, 56, new Item.Settings().maxCount(1)));
    public static final Item REMOTE = register("remote", new ScrapItem(20, 48, new Item.Settings().maxCount(1)));
    public static final Item PILL_BOTTLE = register("pill_bottle", new ScrapItem(16, 40, new Item.Settings().maxCount(1)));
    public static final Item PAINTING = register("painting", new ScrapItem(60, 124, new Item.Settings().maxCount(1)));
    public static final Item LARGE_AXLE = register("large_axle", new ScrapItem(36, 56, new Item.Settings().maxCount(1)));
    public static final Item HAIR_BRUSH = register("hair_brush", new ScrapItem(8, 36, new Item.Settings().maxCount(1)));
    public static final Item CONTROL_PAD = register("control_pad", new ScrapItem(34, 62, new Item.Settings().maxCount(1)));

    private ModItems() {
    }

    public static void register() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(entries -> entries.add(COMPANY_TABLET));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(entries -> entries.add(SCRAP_METAL));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(entries -> {
            entries.add(GOLD_BAR);
            entries.add(SOCCER_BALL);
            entries.add(TOY_CUBE);
            entries.add(V_TYPE_ENGINE);
            entries.add(REMOTE);
            entries.add(PILL_BOTTLE);
            entries.add(PAINTING);
            entries.add(LARGE_AXLE);
            entries.add(HAIR_BRUSH);
            entries.add(CONTROL_PAD);
        });
    }

    private static Item register(String path, Item item) {
        return Registry.register(Registries.ITEM, LethalCompany.id(path), item);
    }
}
