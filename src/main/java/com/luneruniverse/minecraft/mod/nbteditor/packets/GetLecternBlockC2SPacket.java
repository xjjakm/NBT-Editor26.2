package com.luneruniverse.minecraft.mod.nbteditor.packets;

import com.luneruniverse.minecraft.mod.nbteditor.multiversion.IdentifierInst;
import com.luneruniverse.minecraft.mod.nbteditor.multiversion.networking.MVPacket;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.Identifier;

public record GetLecternBlockC2SPacket(int requestId) implements MVPacket {

	public static final Identifier ID = IdentifierInst.of("nbteditor", "get_lectern_block");

	public GetLecternBlockC2SPacket(FriendlyByteBuf payload) {
		this(payload.readVarInt());
	}


	@Override
	public void write(FriendlyByteBuf payload) {
		payload.writeVarInt(requestId);
	}

	@Override
	public Identifier getPacketId() {
		return ID;
	}

}
