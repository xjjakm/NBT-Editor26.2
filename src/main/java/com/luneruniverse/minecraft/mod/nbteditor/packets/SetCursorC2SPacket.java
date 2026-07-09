package com.luneruniverse.minecraft.mod.nbteditor.packets;

import com.luneruniverse.minecraft.mod.nbteditor.multiversion.IdentifierInst;
import com.luneruniverse.minecraft.mod.nbteditor.multiversion.networking.MVPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

public record SetCursorC2SPacket(ItemStack item) implements MVPacket {

	public static final Identifier ID = IdentifierInst.of("nbteditor", "set_cursor");

	public SetCursorC2SPacket(FriendlyByteBuf payload) {
		this(ItemStack.validatedStreamCodec(ItemStack.OPTIONAL_UNTRUSTED_STREAM_CODEC).decode((RegistryFriendlyByteBuf) payload));
	}


	@Override
	public void write(FriendlyByteBuf payload) {
		ItemStack.validatedStreamCodec(ItemStack.OPTIONAL_UNTRUSTED_STREAM_CODEC).encode((RegistryFriendlyByteBuf) payload, item);
	}

	@Override
	public Identifier getPacketId() {
		return ID;
	}

}
