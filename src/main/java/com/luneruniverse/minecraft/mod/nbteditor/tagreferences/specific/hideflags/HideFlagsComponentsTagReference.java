package com.luneruniverse.minecraft.mod.nbteditor.tagreferences.specific.hideflags;

import com.luneruniverse.minecraft.mod.nbteditor.tagreferences.general.TagReference;
import com.luneruniverse.minecraft.mod.nbteditor.tagreferences.specific.data.hideflags.HideFlag;
import net.minecraft.world.item.ItemStack;

import java.util.Map;

public class HideFlagsComponentsTagReference implements TagReference<Map<HideFlag, Boolean>, ItemStack> {

	@Override
	public Map<HideFlag, Boolean> get(ItemStack object) {
		return Map.of();
	}

	@Override
	public void set(ItemStack object, Map<HideFlag, Boolean> value) {

	}
}
