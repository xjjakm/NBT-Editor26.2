package com.luneruniverse.minecraft.mod.nbteditor.multiversion.mixin;

import com.luneruniverse.minecraft.mod.nbteditor.multiversion.MVDrawableParent;
import net.minecraft.client.gui.components.Renderable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Renderable.class)
public interface RenderableMixin extends MVDrawableParent {

}
