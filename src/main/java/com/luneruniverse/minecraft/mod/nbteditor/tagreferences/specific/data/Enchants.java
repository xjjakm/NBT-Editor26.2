package com.luneruniverse.minecraft.mod.nbteditor.tagreferences.specific.data;

import net.minecraft.core.Holder;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.*;

public record Enchants(List<EnchantWithLevel> enchants) {

    /**
     * Holds the enchantment as a Holder to preserve registry context.
     * Using Holder.Direct (from wrapAsHolder) causes serialization failures
     * because RegistryFixedCodec can't serialize holders without a ResourceKey.
     */
    public static record EnchantWithLevel(Holder<Enchantment> enchantHolder, int level) {
        public Enchantment enchant() {
            return enchantHolder.value();
        }
    }

    public Enchants() {
        this(new ArrayList<>());
    }

    public int size() {
        return enchants.size();
    }

    public boolean isEmpty() {
        return size() == 0;
    }


    public int getLevel(Enchantment enchant) {
        return enchants.stream().filter(test -> test.enchant() == enchant)
                .mapToInt(EnchantWithLevel::level).max().orElse(0);
    }

    public void addEnchant(Holder<Enchantment> enchantHolder, int level) {
        enchants.add(new EnchantWithLevel(enchantHolder, level));
    }

    public void addEnchant(EnchantWithLevel enchant) {
        enchants.add(enchant);
    }

    public void addEnchants(Collection<EnchantWithLevel> enchants) {
        this.enchants.addAll(enchants);
    }

    public boolean removeEnchant(Enchantment enchant) {
        return enchants.removeIf(enchantWithLevel -> enchantWithLevel.enchant() == enchant);
    }

    public boolean removeEnchants(Collection<Enchantment> enchants) {
        boolean output = false;
        for (Enchantment enchant : enchants)
            output |= removeEnchant(enchant);
        return output;
    }

    public boolean removeDuplicates() {
        Map<Holder<Enchantment>, Integer> enchants = new LinkedHashMap<>();
        for (EnchantWithLevel enchant : this.enchants)
            enchants.put(enchant.enchantHolder(), enchant.level());
        if (this.enchants.size() == enchants.size())
            return false;
        this.enchants.clear();
        enchants.forEach(this::addEnchant);
        return true;
    }

    public boolean setEnchant(Holder<Enchantment> enchantHolder, int level, boolean onlyUpgrade) {
        if (onlyUpgrade && level <= getLevel(enchantHolder.value())) {
            return false;
        }

        boolean found = false;
        for (ListIterator<EnchantWithLevel> iter = enchants.listIterator(); iter.hasNext(); ) {
            EnchantWithLevel enchantWithLevel = iter.next();
            if (enchantWithLevel.enchantHolder() != enchantHolder)
                continue;
            if (found)
                iter.remove();
            else {
                iter.set(new EnchantWithLevel(enchantHolder, level));
                found = true;
            }
        }
        if (!found)
            addEnchant(enchantHolder, level);
        return true;
    }

    /**
     * Backward-compatible overload that finds the Holder from the Enchantment instance.
     * Prefer the Holder overload to avoid creating unserializable Holder.Direct instances.
     */
    public boolean setEnchant(Enchantment enchant, int level, boolean onlyUpgrade) {
        var registry = com.luneruniverse.minecraft.mod.nbteditor.multiversion.MVRegistry.getEnchantmentRegistry().getInternalValue();
        var holder = registry.getResourceKey(enchant)
                .flatMap(registry::get)
                .map(h -> (net.minecraft.core.Holder<Enchantment>) h)
                .orElseGet(() -> registry.wrapAsHolder(enchant));
        return setEnchant(holder, level, onlyUpgrade);
    }

    public void setEnchant(EnchantWithLevel enchant, boolean onlyUpgrade) {
        setEnchant(enchant.enchantHolder(), enchant.level(), onlyUpgrade);
    }

    public void setEnchants(Collection<EnchantWithLevel> enchants, boolean onlyUpgrade) {
        for (EnchantWithLevel enchant : enchants)
            setEnchant(enchant, onlyUpgrade);
    }

    public void replaceEnchants(Collection<EnchantWithLevel> enchants) {
        this.enchants.clear();
        this.enchants.addAll(enchants);
    }

    public void clearEnchants() {
        enchants.clear();
    }

}
