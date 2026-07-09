package com.luneruniverse.minecraft.mod.nbteditor.multiversion;

public class MVMatrix4f {
	
	public static final Class<?> Matrix4f_class = Version.<Class<?>>newSwitch()
			.range("1.19.3", null, () -> Reflection.getClass("org.joml.Matrix4f"))
			.range(null, "1.19.2", () -> Reflection.getClass("net.minecraft.class_1159"))
			.get();
	public static final Class<?> Matrix4fc_class = Version.<Class<?>>newSwitch()
			.range("1.19.3", null, () -> Reflection.getClass("org.joml.Matrix4fc"))
			.range(null, "1.19.2", Matrix4f_class)
			.get();

	
}
