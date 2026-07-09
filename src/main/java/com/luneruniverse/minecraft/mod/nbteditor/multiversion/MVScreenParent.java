package com.luneruniverse.minecraft.mod.nbteditor.multiversion;

import net.minecraft.client.gui.screens.Screen;
import org.joml.Matrix3x2fStack;

public interface MVScreenParent {
	public default void renderBackground(Matrix3x2fStack matrices) {
		MVDrawableHelper.renderBackground((Screen) this, matrices);
	}
}
