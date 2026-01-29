package dev.saturn.lethalcompany.registry;

import net.minecraft.client.option.KeyBinding;
import org.lwjgl.glfw.GLFW;

public final class ModKeybinds {
    public static final KeyBinding OPEN_COMPANY_INVENTORY = new KeyBinding(
            "key.lethalcompany.open_company_inventory",
            GLFW.GLFW_KEY_G,
            "category.lethalcompany"
    );
    public static final KeyBinding SCAN = new KeyBinding(
            "key.lethalcompany.scan",
            GLFW.GLFW_KEY_R,
            "category.lethalcompany"
    );

    private ModKeybinds() {
    }
}
