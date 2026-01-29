package dev.saturn.lethalcompany.network;

import dev.saturn.lethalcompany.LethalCompany;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record CompanyInventorySyncPayload(ItemStack slot0, ItemStack slot1, ItemStack slot2, ItemStack slot3) implements CustomPayload {
    public static final Id<CompanyInventorySyncPayload> ID = new Id<>(LethalCompany.id("company_inventory_sync"));

    public static final PacketCodec<RegistryByteBuf, CompanyInventorySyncPayload> CODEC = new PacketCodec<>() {
        private static ItemStack decodeStack(RegistryByteBuf buf) {
            boolean present = buf.readBoolean();
            return present ? ItemStack.PACKET_CODEC.decode(buf) : ItemStack.EMPTY;
        }

        private static void encodeStack(RegistryByteBuf buf, ItemStack stack) {
            boolean present = !stack.isEmpty();
            buf.writeBoolean(present);
            if (present) {
                ItemStack.PACKET_CODEC.encode(buf, stack);
            }
        }

        @Override
        public CompanyInventorySyncPayload decode(RegistryByteBuf buf) {
            return new CompanyInventorySyncPayload(
                    decodeStack(buf),
                    decodeStack(buf),
                    decodeStack(buf),
                    decodeStack(buf)
            );
        }

        @Override
        public void encode(RegistryByteBuf buf, CompanyInventorySyncPayload value) {
            encodeStack(buf, value.slot0());
            encodeStack(buf, value.slot1());
            encodeStack(buf, value.slot2());
            encodeStack(buf, value.slot3());
        }
    };

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
