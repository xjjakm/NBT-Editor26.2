package com.luneruniverse.minecraft.mod.nbteditor.mixin.toggled;

import com.luneruniverse.minecraft.mod.nbteditor.NBTEditorClient;
import com.luneruniverse.minecraft.mod.nbteditor.screens.containers.ClientHandledScreen;
import com.luneruniverse.minecraft.mod.nbteditor.util.MainUtil;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundSetCursorItemPacket;
import net.minecraft.network.protocol.game.ClientboundSetPlayerInventoryPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {
	
	@Inject(method = "handleSetCursorItem", at = @At("HEAD"), cancellable = true)
	private void onSetCursorItem(ClientboundSetCursorItemPacket packet, CallbackInfo info) {
		if (!MainUtil.client.isSameThread())
			return;
		
		if (NBTEditorClient.CURSOR_MANAGER.isBranched()) {
			info.cancel();
			
			if (!(NBTEditorClient.CURSOR_MANAGER.getCurrentRoot() instanceof CreativeModeInventoryScreen)) {
				var player = MainUtil.client.player;
				if (player != null)
					player.containerMenu.setCarried(packet.contents());
			}
		}
	}
	
	@Inject(method = "handleSetPlayerInventory", at = @At("RETURN"))
	private void onSetPlayerInventory_return(ClientboundSetPlayerInventoryPacket packet, CallbackInfo info) {
		if (MainUtil.client.gui.screen() instanceof ClientHandledScreen clientHandledScreen)
			clientHandledScreen.getServerInventoryManager().onSetPlayerInventoryPacket(packet);
	}
	
}
