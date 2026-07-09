package com.luneruniverse.minecraft.mod.nbteditor.multiversion.mixin.toggled;

import com.luneruniverse.minecraft.mod.nbteditor.misc.MixinLink;
import net.minecraft.client.gui.screens.inventory.BookViewScreen;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BookViewScreen.BookAccess.class)
public class BookScreenContentsMixin {
	@Inject(method = "fromItem", at = @At("RETURN"))
	private static void create(ItemStack item, CallbackInfoReturnable<BookViewScreen.BookAccess> info) {
		if (item.has(DataComponents.WRITTEN_BOOK_CONTENT))
			MixinLink.WRITTEN_BOOK_CONTENTS.put(info.getReturnValue(), true);
	}
}
