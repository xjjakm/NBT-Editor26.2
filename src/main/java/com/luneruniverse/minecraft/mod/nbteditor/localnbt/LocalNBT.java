package com.luneruniverse.minecraft.mod.nbteditor.localnbt;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix3x2fStack;

import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

public interface LocalNBT {
	public static Optional<LocalNBT> deserialize(CompoundTag nbt, int defaultDataVersion) {
		return Optional.ofNullable(switch (nbt.getString("type").orElse("item")) {
			case "item" -> LocalItemStack.deserialize(nbt, defaultDataVersion);
			case "block" -> LocalBlock.deserialize(nbt, defaultDataVersion);
			case "entity" -> LocalEntity.deserialize(nbt, defaultDataVersion);
			default -> null;
		});
	}
	
	@SuppressWarnings("unchecked")
    static <T extends LocalNBT> T copy(T localNBT) {
		return (T) localNBT.copy();
	}

	
	default boolean isEmpty() {
		return isEmpty(getId());
	}
	boolean isEmpty(Identifier id);
	
	Component getName();
	void setName(Component name);
	String getDefaultName();
	
	Identifier getId();
	void setId(Identifier id);
	Set<Identifier> getIdOptions();
	
	CompoundTag getNBT();
	void setNBT(CompoundTag nbt);
	default CompoundTag getOrCreateNBT() {
		CompoundTag nbt = getNBT();
		if (nbt == null) {
			nbt = new CompoundTag();
			setNBT(nbt);
		}
		return nbt;
	}
	default void modifyNBT(Consumer<CompoundTag> modifier) {
		CompoundTag nbt = getNBT();
		if (nbt == null)
			nbt = new CompoundTag();
		modifier.accept(nbt);
		setNBT(nbt);
	}
	
	void renderIcon(Matrix3x2fStack matrices, int x, int y, float tickDelta);
	
	Optional<ItemStack> toItem(boolean cleanup);
	CompoundTag serialize();
	Component toHoverableText();
	
	LocalNBT copy();
	@Override
    boolean equals(Object nbt);
}
