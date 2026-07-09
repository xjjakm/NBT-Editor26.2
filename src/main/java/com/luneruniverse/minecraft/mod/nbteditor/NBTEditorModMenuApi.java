package com.luneruniverse.minecraft.mod.nbteditor;

import com.luneruniverse.minecraft.mod.nbteditor.screens.ConfigScreen;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

import net.minecraft.client.gui.screens.Screen;

public class NBTEditorModMenuApi implements ModMenuApi {
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return (ConfigScreenFactory<Screen>) parent -> new ConfigScreen(parent);
	}
}
