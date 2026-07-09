package com.luneruniverse.minecraft.mod.nbteditor.mixin;

import com.luneruniverse.minecraft.mod.nbteditor.NBTEditorClient;
import com.luneruniverse.minecraft.mod.nbteditor.commands.factories.BookCommand;
import com.luneruniverse.minecraft.mod.nbteditor.multiversion.MVDrawableHelper;
import com.luneruniverse.minecraft.mod.nbteditor.multiversion.MVMisc;
import com.luneruniverse.minecraft.mod.nbteditor.multiversion.ScreenTexts;
import com.luneruniverse.minecraft.mod.nbteditor.multiversion.TextInst;
import com.luneruniverse.minecraft.mod.nbteditor.nbtreferences.BlockReference;
import com.luneruniverse.minecraft.mod.nbteditor.nbtreferences.itemreferences.ContainerItemReference;
import com.luneruniverse.minecraft.mod.nbteditor.nbtreferences.itemreferences.ItemReference;
import com.luneruniverse.minecraft.mod.nbteditor.screens.OverlaySupportingScreen;
import com.luneruniverse.minecraft.mod.nbteditor.util.MainUtil;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.BookViewScreen;
import net.minecraft.client.gui.screens.inventory.BookViewScreen.BookAccess;
import net.minecraft.client.gui.screens.inventory.LecternScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Group;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@Mixin(BookViewScreen.class)
public class BookViewScreenMixin extends Screen {
	
	@Shadow
	private BookAccess bookAccess;
	@Shadow
	private int currentPage;
	
	@Unique
	private boolean renderLogo;
	@Unique
	private Button openBtn;
	@Unique
	private Button convertBtn;
	
	protected BookViewScreenMixin() {
		super(Component.empty());
	}
	
	@Unique
	private CompletableFuture<Optional<ItemReference>> getReference() {
		if ((Object) this instanceof LecternScreen) {
			return BlockReference.getLecternBlock().thenApply(optionalRef -> {
				if (optionalRef.isEmpty()) {
					MainUtil.client.player.sendSystemMessage(TextInst.translatable("nbteditor.no_ref.unknown"));
					return Optional.empty();
				}
				return optionalRef.map(ref -> new ContainerItemReference<>(ref, 0));
			});
		}
		
		try {
			return CompletableFuture.completedFuture(Optional.of(ItemReference.getHeldItem()));
		} catch (CommandSyntaxException e) {
			MainUtil.client.player.sendSystemMessage(TextInst.literal(e.getMessage()).formatted(ChatFormatting.RED));
			return CompletableFuture.completedFuture(Optional.empty());
		}
	}
	@Unique
	private void getReference(Consumer<ItemReference> consumer) {
		getReference().thenAccept(ref -> MainUtil.client.execute(() -> ref.ifPresent(consumer)));
	}
	
	@Unique
	private void updateButtons(BookAccess contents) {
		boolean editable = NBTEditorClient.SERVER_CONN.isEditingAllowed() && MVMisc.isWrittenBookContents(contents);
		renderLogo = editable;
		openBtn.visible = editable;
		convertBtn.visible = editable;
	}
	
	@Inject(method = "init", at = @At("TAIL"))
	private void init(CallbackInfo info) {
		if (MainUtil.client.gui.screen() instanceof
				com.luneruniverse.minecraft.mod.nbteditor.screens.factories.BookScreen) { // Preview mode
			renderLogo = true;
			return;
		}
		
		openBtn = addRenderableWidget(MVMisc.newButton(16, 64, 100, 20, TextInst.translatable("nbteditor.book.open"), _ -> getReference(ref ->
				MainUtil.client.gui.setScreen(
						new com.luneruniverse.minecraft.mod.nbteditor.screens.factories.BookScreen(ref, Math.max(0, currentPage))))));
		convertBtn = addRenderableWidget(MVMisc.newButton(16, 64 + 24, 100, 20, TextInst.translatable("nbteditor.book.convert"),
				_ -> getReference(itemRef -> {
					if (BookCommand.convertBookToWritable(itemRef)) {
						openBtn.visible = false;
						convertBtn.visible = false;
						onClose();
					}
				})));
		
		updateButtons(bookAccess);
	}
	
	@Inject(method = "setBookAccess", at = @At("HEAD"))
	private void setPageProvider(BookAccess contents, CallbackInfo info) {
		updateButtons(contents);
	}
	
	@Inject(method = "createMenuControls", at = @At("HEAD"), cancellable = true)
	private void addCloseButton(CallbackInfo info) {
		if (MainUtil.client.gui.screen() instanceof
				com.luneruniverse.minecraft.mod.nbteditor.screens.factories.BookScreen) { // Preview mode
			info.cancel();
			addRenderableWidget(MVMisc.newButton(width / 2 - 100, 196, 200, 20, ScreenTexts.DONE,
					_ -> OverlaySupportingScreen.setOverlayStatic(null)));
		}
	}

    @Inject(method = "extractRenderState", at = @At("TAIL"))
	@Group(name = "render", min = 1)
	private void render(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta, CallbackInfo info) {
		if (renderLogo)
			MainUtil.renderLogo(MVDrawableHelper.getMatrices(context));
	}
	
}
