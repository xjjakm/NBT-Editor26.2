package com.luneruniverse.minecraft.mod.nbteditor.mixin;

import com.luneruniverse.minecraft.mod.nbteditor.NBTEditor;
import com.luneruniverse.minecraft.mod.nbteditor.NBTEditorClient;
import com.luneruniverse.minecraft.mod.nbteditor.multiversion.IgnoreCloseScreenPacket;
import com.luneruniverse.minecraft.mod.nbteditor.multiversion.MVMisc;
import com.luneruniverse.minecraft.mod.nbteditor.screens.containers.ClientHandledScreen;
import com.luneruniverse.minecraft.mod.nbteditor.screens.containers.ClientScreenHandler;
import com.luneruniverse.minecraft.mod.nbteditor.util.MainUtil;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundContainerClosePacket;
import net.minecraft.network.protocol.game.ClientboundContainerSetContentPacket;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {

	@Unique
	private static boolean updatingClientInventory;

	@SuppressWarnings("unchecked")
	@Inject(method = "handleContainerContent", at = @At("HEAD"), cancellable = true)
	private void onInventory(ClientboundContainerSetContentPacket packet, CallbackInfo info) {
		if (!MainUtil.client.isSameThread() || updatingClientInventory)
			return;

		if (MVMisc.getSyncId(packet) == ClientScreenHandler.SYNC_ID) {
			NBTEditor.LOGGER.warn("Ignoring an inventory packet with a ClientHandledScreen sync id!");
			info.cancel();
			return;
		}

		if (NBTEditorClient.CURSOR_MANAGER.isBranched()) {
			info.cancel();

			var player = MainUtil.client.player;
			if (player != null) {
				try {
					updatingClientInventory = true;
					player.containerMenu = NBTEditorClient.CURSOR_MANAGER.getCurrentRoot().getMenu();
					((ClientPacketListener) (Object) this).handleContainerContent(packet);
				} finally {
					updatingClientInventory = false;
					player.containerMenu = NBTEditorClient.CURSOR_MANAGER.getCurrentBranch().getMenu();
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Inject(method = "handleContainerSetSlot", at = @At("HEAD"), cancellable = true)
	private void onScreenHandlerSlotUpdate(ClientboundContainerSetSlotPacket packet, CallbackInfo info) {
		if (!MainUtil.client.isSameThread() || updatingClientInventory)
			return;

		if (packet.getContainerId() == ClientScreenHandler.SYNC_ID) {
			NBTEditor.LOGGER.warn("Ignoring a slot update packet with a ClientHandledScreen sync id!");
			info.cancel();
			return;
		}

		if (NBTEditorClient.CURSOR_MANAGER.isBranched()) {
			info.cancel();

			var player = MainUtil.client.player;

			if (packet.getContainerId() == -1) {
				if (!(NBTEditorClient.CURSOR_MANAGER.getCurrentRoot() instanceof CreativeModeInventoryScreen) && player != null)
					player.containerMenu.setCarried(packet.getItem());
				return;
			}

			if (player != null) {
				try {
					updatingClientInventory = true;
					player.containerMenu = NBTEditorClient.CURSOR_MANAGER.getCurrentRoot().getMenu();
					((ClientPacketListener) (Object) this).handleContainerSetSlot(packet);
				} finally {
					updatingClientInventory = false;
					player.containerMenu = NBTEditorClient.CURSOR_MANAGER.getCurrentBranch().getMenu();
				}
			}
		}
	}

	@Inject(method = "handleContainerContent", at = @At("RETURN"))
	private void onInventory_return(ClientboundContainerSetContentPacket packet, CallbackInfo info) {
		if (MainUtil.client.gui.screen() instanceof ClientHandledScreen clientHandledScreen)
			clientHandledScreen.getServerInventoryManager().onInventoryPacket(packet);
	}

	@Inject(method = "handleContainerSetSlot", at = @At("RETURN"))
	private void onScreenHandlerSlotUpdate_return(ClientboundContainerSetSlotPacket packet, CallbackInfo info) {
		if (MainUtil.client.gui.screen() instanceof ClientHandledScreen clientHandledScreen)
			clientHandledScreen.getServerInventoryManager().onScreenHandlerSlotUpdatePacket(packet);
	}

	@Inject(method = "handleContainerClose", at = @At("HEAD"), cancellable = true)
	private void onCloseScreen(ClientboundContainerClosePacket packet, CallbackInfo info) {
		if (!MainUtil.client.isSameThread())
			return;

		if (packet.getContainerId() == ClientScreenHandler.SYNC_ID) {
			NBTEditor.LOGGER.warn("Ignoring a close screen packet with a ClientHandledScreen sync id!");
			info.cancel();
			return;
		}

		NBTEditorClient.CURSOR_MANAGER.onCloseScreenPacket();

		if (MainUtil.client.gui.screen() instanceof IgnoreCloseScreenPacket)
			info.cancel();
	}

}
