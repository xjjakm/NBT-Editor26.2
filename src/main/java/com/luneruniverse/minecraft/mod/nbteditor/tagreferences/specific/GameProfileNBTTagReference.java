package com.luneruniverse.minecraft.mod.nbteditor.tagreferences.specific;

import com.luneruniverse.minecraft.mod.nbteditor.tagreferences.general.TagReference;
import com.mojang.authlib.GameProfile;
import net.minecraft.nbt.CompoundTag;

import java.util.Optional;

public class GameProfileNBTTagReference implements TagReference<Optional<GameProfile>, CompoundTag> {


	@Override
	public Optional<GameProfile> get(CompoundTag object) {
		return Optional.empty();
	}

	@Override
	public void set(CompoundTag object, Optional<GameProfile> value) {

	}
}
