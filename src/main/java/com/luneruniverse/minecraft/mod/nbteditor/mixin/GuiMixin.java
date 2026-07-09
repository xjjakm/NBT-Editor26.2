package com.luneruniverse.minecraft.mod.nbteditor.mixin;

import com.luneruniverse.minecraft.mod.nbteditor.NBTEditorClient;
import com.luneruniverse.minecraft.mod.nbteditor.async.UpdateCheckerThread;
import com.luneruniverse.minecraft.mod.nbteditor.misc.MixinLink;
import com.luneruniverse.minecraft.mod.nbteditor.server.ServerMixinLink;
import com.luneruniverse.minecraft.mod.nbteditor.util.MainUtil;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.screens.LoadingOverlay;
import net.minecraft.client.gui.screens.Overlay;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class GuiMixin {
	
	@Inject(method = "setOverlay", at = @At("HEAD"))
	private void nbteditor_setOverlay(Overlay overlay, CallbackInfo info) {
		if (((Gui) (Object) this).overlay() instanceof LoadingOverlay && overlay == null && !MixinLink.CLIENT_LOADED) {
			MixinLink.CLIENT_LOADED = true;
			new UpdateCheckerThread().start();
		}
	}
	
	@Inject(method = "setScreen", at = @At("HEAD"))
	private void nbteditor_setScreen(Screen screen, CallbackInfo info) {
		if (screen == null) {
			if (NBTEditorClient.CURSOR_MANAGER != null)
				NBTEditorClient.CURSOR_MANAGER.onNoScreenSet();
		} else if (screen instanceof AbstractContainerScreen<?> handledScreen) {
			for (Slot slot : handledScreen.getMenu().slots)
				ServerMixinLink.SLOT_OWNER.put(slot, MainUtil.client.player);
			if (NBTEditorClient.CURSOR_MANAGER != null)
				NBTEditorClient.CURSOR_MANAGER.onHandledScreenSet(handledScreen);
		}
	}
	
}
