package com.luneruniverse.minecraft.mod.nbteditor.multiversion;

import net.minecraft.client.gui.components.Renderable;
import org.joml.Matrix3x2fStack;

public interface MVDrawableParent {
	public default void render(Matrix3x2fStack matrices, int mouseX, int mouseY, float delta) {
		MVDrawableHelper.render((Renderable) this, matrices, mouseX, mouseY, delta);
	}
}
