package com.luneruniverse.minecraft.mod.nbteditor.tagreferences.specific;

import com.luneruniverse.minecraft.mod.nbteditor.multiversion.MVComponentType;
import com.luneruniverse.minecraft.mod.nbteditor.multiversion.MVMisc;
import com.luneruniverse.minecraft.mod.nbteditor.multiversion.MVRegistry;
import com.luneruniverse.minecraft.mod.nbteditor.multiversion.Version;
import com.luneruniverse.minecraft.mod.nbteditor.tagreferences.general.ComponentTagReference;
import com.luneruniverse.minecraft.mod.nbteditor.tagreferences.general.TagReference;
import com.luneruniverse.minecraft.mod.nbteditor.tagreferences.specific.data.Enchants;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.ItemEnchantments;

import java.util.stream.Collectors;

public class EnchantsTagReference implements TagReference<Enchants, ItemStack> {
	
	private static TagReference<Enchants, ItemStack> getEnchantsTagRef(String tag, MVComponentType<ItemEnchantments> component) {
		return Version.<TagReference<Enchants, ItemStack>>newSwitch()
				.range("1.20.5", null, () -> new ComponentTagReference<>(component,
						null,
						componentValue -> componentValue == null ? new Enchants() : new Enchants(componentValue.entrySet().stream()
								.map(entry -> new Enchants.EnchantWithLevel(entry.getKey().value(), entry.getIntValue())).collect(Collectors.toList())),
						(componentValue, enchants) -> (ItemEnchantments) MVMisc.withEnchantments(componentValue,
								new Object2IntOpenHashMap<>(enchants.enchants().stream().collect(Collectors.toMap(
										enchant -> MVRegistry.getEnchantmentRegistry().getInternalValue().wrapAsHolder(enchant.enchant()),
										enchant -> Math.min(255, enchant.level()),
										Math::max))))))
				.get();
	}
	
	private static final TagReference<Enchants, ItemStack> ENCHANTMENTS = getEnchantsTagRef("Enchantments", MVComponentType.ENCHANTMENTS);
	private static final TagReference<Enchants, ItemStack> STORED_ENCHANTMENTS = getEnchantsTagRef("StoredEnchantments", MVComponentType.STORED_ENCHANTMENTS);
	
	public EnchantsTagReference() {
		
	}
	
	@Override
	public Enchants get(ItemStack object) {
		if (object.is(Items.ENCHANTED_BOOK))
			return STORED_ENCHANTMENTS.get(object);
		return ENCHANTMENTS.get(object);
	}
	
	@Override
	public void set(ItemStack object, Enchants value) {
		if (object.is(Items.ENCHANTED_BOOK))
			STORED_ENCHANTMENTS.set(object, value);
		else
			ENCHANTMENTS.set(object, value);
	}
	
}
