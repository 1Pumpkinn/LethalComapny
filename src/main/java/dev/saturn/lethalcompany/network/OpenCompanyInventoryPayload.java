package dev.saturn.lethalcompany.network;

import dev.saturn.lethalcompany.LethalCompany;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record OpenCompanyInventoryPayload() implements CustomPayload {
    public static final Id<OpenCompanyInventoryPayload> ID = new Id<>(LethalCompany.id("open_company_inventory"));
    public static final PacketCodec<PacketByteBuf, OpenCompanyInventoryPayload> CODEC = PacketCodec.unit(new OpenCompanyInventoryPayload());

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
