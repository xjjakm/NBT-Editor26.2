package com.luneruniverse.minecraft.mod.nbteditor.packets;

import com.luneruniverse.minecraft.mod.nbteditor.multiversion.IdentifierInst;
import com.luneruniverse.minecraft.mod.nbteditor.util.BlockStateProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public record ViewBlockS2CPacket(int requestId, ResourceKey<Level> world, BlockPos pos, Identifier id,
                                 BlockStateProperties state, CompoundTag nbt) implements ResponsePacket {

	public static final Identifier ID = IdentifierInst.of("nbteditor", "view_block");

	public ViewBlockS2CPacket {
		if ((world == null) != (pos == null))
			throw new IllegalArgumentException("world and pos have to be null together!");
		if ((id == null) != (state == null) || (id == null) != (nbt == null))
			throw new IllegalArgumentException("id, state, and nbt have to be null together!");

	}

	public static ViewBlockS2CPacket read(FriendlyByteBuf payload) {
		int requestId = payload.readVarInt();
		ResourceKey<Level> world = null;
		BlockPos pos = null;
		if (payload.readBoolean()) {
			world = payload.readResourceKey(Registries.DIMENSION);
			pos = payload.readBlockPos();
		}
		Identifier id = null;
		BlockStateProperties state = null;
		CompoundTag nbt = null;
		if (payload.readBoolean()) {
			id = payload.readIdentifier();
			state = new BlockStateProperties(payload);
			nbt = payload.readNbt();
		}
		return new ViewBlockS2CPacket(requestId, world, pos, id, state, nbt);
	}


	public boolean foundBlock() {
		return id != null;
	}


	@Override
	public void write(FriendlyByteBuf payload) {
		payload.writeVarInt(requestId);
		if (world == null) {
			payload.writeBoolean(false);
		} else {
			payload.writeBoolean(true);
			payload.writeResourceKey(world);
			payload.writeBlockPos(pos);
		}
		if (id == null) {
			payload.writeBoolean(false);
		} else {
			payload.writeBoolean(true);
			payload.writeIdentifier(id);
			state.writeToPayload(payload);
			payload.writeNbt(nbt);
		}
	}

	@Override
	public Identifier getPacketId() {
		return ID;
	}

}
