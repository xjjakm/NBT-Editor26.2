package com.luneruniverse.minecraft.mod.nbteditor.packets;

import com.luneruniverse.minecraft.mod.nbteditor.multiversion.IdentifierInst;
import com.luneruniverse.minecraft.mod.nbteditor.multiversion.networking.MVPacket;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.Identifier;

public record ProtocolVersionS2CPacket(int version) implements MVPacket {

	public static final Identifier ID = IdentifierInst.of("nbteditor", "protocol_version");

	public ProtocolVersionS2CPacket(FriendlyByteBuf payload) {
		this(payload.readVarInt());
	}


	@Override
	public void write(FriendlyByteBuf payload) {
		payload.writeVarInt(version);
	}

	@Override
	public Identifier getPacketId() {
		return ID;
	}

}
