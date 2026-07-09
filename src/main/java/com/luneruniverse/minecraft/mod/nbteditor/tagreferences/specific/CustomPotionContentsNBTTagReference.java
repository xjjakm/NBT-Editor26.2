package com.luneruniverse.minecraft.mod.nbteditor.tagreferences.specific;

import com.luneruniverse.minecraft.mod.nbteditor.tagreferences.general.TagReference;
import com.luneruniverse.minecraft.mod.nbteditor.tagreferences.specific.data.CustomPotionContents;

import net.minecraft.world.item.ItemStack;

public class CustomPotionContentsNBTTagReference implements TagReference<CustomPotionContents, ItemStack> {


	@Override
	public CustomPotionContents get(ItemStack object) {
		return null;
	}

	@Override
	public void set(ItemStack object, CustomPotionContents value) {

	}
}
