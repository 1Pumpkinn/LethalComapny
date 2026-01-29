package dev.saturn.lethalcompany.network;

import dev.saturn.lethalcompany.LethalCompany;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record PickupItemPayload(java.util.UUID entityUuid) implements CustomPayload {
    public static final Id<PickupItemPayload> ID = new Id<>(LethalCompany.id("pickup_item"));
    public static final PacketCodec<PacketByteBuf, PickupItemPayload> CODEC = new PacketCodec<>() {
        @Override
        public PickupItemPayload decode(PacketByteBuf buf) {
            return new PickupItemPayload(buf.readUuid());
        }

        @Override
        public void encode(PacketByteBuf buf, PickupItemPayload value) {
            buf.writeUuid(value.entityUuid());
        }
    };

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
