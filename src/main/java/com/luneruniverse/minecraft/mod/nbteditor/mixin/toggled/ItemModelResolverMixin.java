package com.luneruniverse.minecraft.mod.nbteditor.mixin.toggled;

import com.luneruniverse.minecraft.mod.nbteditor.misc.MixinLink;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemModelResolver.class)
public class ItemModelResolverMixin {
	
	@Inject(method = "appendItemLayers", at = @At("HEAD"))
	private void update(ItemStackRenderState renderState, ItemStack stack, ItemDisplayContext displayContext, Level world, ItemOwner heldItemContext, int seed, CallbackInfo ci) {
		MixinLink.ITEM_BEING_RENDERED.put(Thread.currentThread(), stack);
	}
	
}
