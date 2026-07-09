package com.luneruniverse.minecraft.mod.nbteditor.mixin;

import com.luneruniverse.minecraft.mod.nbteditor.screens.widgets.FormattedTextFieldWidget;
import com.luneruniverse.minecraft.mod.nbteditor.util.MainUtil;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(KeyboardHandler.class)
public class KeyboardHandlerMixin {
	@Redirect(method = "keyPress", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/GameNarrator;isActive()Z"))
	private boolean isActive(GameNarrator manager) {
		if (MainUtil.client.gui.screen() != null) {
			GuiEventListener focused = MainUtil.client.gui.screen().getFocused();
			while (focused != null) {
				if (focused instanceof FormattedTextFieldWidget)
					return false;
				else if (focused instanceof ContainerEventHandler parent)
					focused = parent.getFocused();
				else
					break;
			}
		}
		return manager.isActive();
	}
}
