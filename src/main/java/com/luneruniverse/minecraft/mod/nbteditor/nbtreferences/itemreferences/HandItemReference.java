package com.luneruniverse.minecraft.mod.nbteditor.nbtreferences.itemreferences;

import com.luneruniverse.minecraft.mod.nbteditor.NBTEditorClient;
import com.luneruniverse.minecraft.mod.nbteditor.util.MainUtil;
import com.luneruniverse.minecraft.mod.nbteditor.util.SlotUtil;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

public record HandItemReference(InteractionHand hand) implements ItemReference {


	@Override
	public boolean exists() {
		return true;
	}

	@Override
	public ItemStack getItem() {
		return MainUtil.client.player.getItemInHand(hand);
	}

	@Override
	public void saveItem(ItemStack toSave, Runnable onFinished) {
		MainUtil.saveItem(hand, toSave);
		onFinished.run();
	}

	@Override
	public boolean isLocked() {
		return false;
	}

	@Override
	public boolean isLockable() {
		return false;
	}

	@Override
	public int getBlockedSlot() {
		if (hand == InteractionHand.MAIN_HAND)
			return SlotUtil.createHotbarInInv(MainUtil.client.player.getInventory().selected);
		return SlotUtil.createOffHandInInv();
	}

	@Override
	public void showParent() {
		NBTEditorClient.CURSOR_MANAGER.closeRoot();
	}

}
