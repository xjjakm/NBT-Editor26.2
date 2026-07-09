package com.luneruniverse.minecraft.mod.nbteditor.multiversion.mixin;

import com.luneruniverse.minecraft.mod.nbteditor.multiversion.MVElement;
import com.luneruniverse.minecraft.mod.nbteditor.multiversion.OldEventBehavior;
import com.luneruniverse.minecraft.mod.nbteditor.util.MainUtil;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerEventHandler.class)
public class AbstractContainerEventHandlerMixin {
	@Inject(method = "setFocused", at = @At("RETURN"))
	private void setFocused(GuiEventListener element, CallbackInfo info) {
		boolean oldEvents = MainUtil.client.gui.screen() instanceof OldEventBehavior;
		for (GuiEventListener child : ((AbstractContainerEventHandler) (Object) this).children()) {
			if (child instanceof MVElement multiChild)
				multiChild.setMultiFocused(child == element);
			if (oldEvents && child instanceof EditBox textChild)
				textChild.setFocused(child == element);
		}
	}
}
