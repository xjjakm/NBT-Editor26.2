package com.luneruniverse.minecraft.mod.nbteditor.containers;

import com.luneruniverse.minecraft.mod.nbteditor.localnbt.LocalBlock;
import com.luneruniverse.minecraft.mod.nbteditor.multiversion.Version;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;

public record ItemBlockContainerIO(ContainerIO<ItemStack> item, ContainerIO<LocalBlock> block) {
	
	public static ItemBlockContainerIO forSlotKeyItems(int numSlots) {
		return Version.<ItemBlockContainerIO>newSwitch()
				.range("1.20.5", null, () -> new ItemBlockContainerIO(
                        new ContainerComponentContainerIO(numSlots),
                        ContainerIO.forLocalNBT(new SlotKeyNbtListContainerIO(numSlots).forNbtCompoundItems())))
				.get();
	}
	
	public static ItemBlockContainerIO forKeys(String entityId, String... keys) {
		return new ItemBlockContainerIO(
				ContainerIO.forItemStackBlockEntityTag(new ItemEntityContainerIO.BlockEntityContainerIO(true, keys), entityId),
				ContainerIO.forLocalNBT(new KeysContainerIO(false, keys)));
	}
	public static ItemBlockContainerIO forKeys(BlockEntityType<?> entityId, String... keys) {
		return forKeys(BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(entityId).toString(), keys);
	}
	
	public ItemBlockContainerIO withTextures(Identifier... textures) {
		return new ItemBlockContainerIO(item.withTextures(textures), block.withTextures(textures));
	}
	
}
