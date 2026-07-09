package com.luneruniverse.minecraft.mod.nbteditor.commands.get;

import com.luneruniverse.minecraft.mod.nbteditor.commands.ClientCommand;
import com.luneruniverse.minecraft.mod.nbteditor.commands.arguments.EnumArgumentType;
import com.luneruniverse.minecraft.mod.nbteditor.containers.ContainerIOs;
import com.luneruniverse.minecraft.mod.nbteditor.multiversion.TextInst;
import com.luneruniverse.minecraft.mod.nbteditor.multiversion.commands.FabricClientCommandSource;
import com.luneruniverse.minecraft.mod.nbteditor.tagreferences.ItemTagReferences;
import com.luneruniverse.minecraft.mod.nbteditor.tagreferences.specific.data.hideflags.HideFlag;
import com.luneruniverse.minecraft.mod.nbteditor.util.MainUtil;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.Nullable;
import tsp.headdb.ported.Category;
import tsp.headdb.ported.Head;
import tsp.headdb.ported.HeadAPI;
import tsp.headdb.ported.inventory.InventoryUtils;

import java.util.Map;

import static com.luneruniverse.minecraft.mod.nbteditor.multiversion.commands.ClientCommandManager.argument;
import static com.luneruniverse.minecraft.mod.nbteditor.multiversion.commands.ClientCommandManager.literal;

public class GetHdbCommand extends ClientCommand {
	
	@Override
	public String getName() {
		return "hdb";
	}
	
	@Override
	public String getExtremeAlias() {
		return "h";
	}

	public static Block getBlock(@Nullable DyeColor dyeColor) {
		if (dyeColor == null) {
			return Blocks.SHULKER_BOX;
		} else {
			// 26.2: dyed shulker boxes moved to Blocks.DYED_SHULKER_BOX (ColorCollection)
			return Blocks.DYED_SHULKER_BOX.pick(dyeColor);
		}
	}

	@Override
	public void register(LiteralArgumentBuilder<FabricClientCommandSource> builder, String path) {
		builder.then(literal("search").then(argument("query", StringArgumentType.greedyString()).executes(context -> {
					if (HeadAPI.checkUpdated())
						HeadAPI.openSearchDatabase(context.getArgument("query", String.class));
					return Command.SINGLE_SUCCESS;
				})))
				.then(literal("tagsearch").then(argument("query", StringArgumentType.greedyString()).executes(context -> {
					if (HeadAPI.checkUpdated())
						HeadAPI.openTagSearchDatabase(context.getArgument("query", String.class));
					return Command.SINGLE_SUCCESS;
				})))
				.then(literal("id").then(argument("id", IntegerArgumentType.integer(1))
						.then(argument("amount", IntegerArgumentType.integer(1)).executes(context -> {
							if (!HeadAPI.checkUpdated())
								return Command.SINGLE_SUCCESS;
							Head head = HeadAPI.getHeadByID(context.getArgument("id", Integer.class));
							if (head == null)
								context.getSource().sendFeedback(TextInst.translatable("nbteditor.hdb.head_not_found"));
							else
								InventoryUtils.purchaseHead(head, context.getArgument("amount", Integer.class), "", "");
							return Command.SINGLE_SUCCESS;
						}))
					.executes(context -> {
						if (!HeadAPI.checkUpdated())
							return Command.SINGLE_SUCCESS;
						Head head = HeadAPI.getHeadByID(context.getArgument("id", Integer.class));
						if (head == null)
							context.getSource().sendFeedback(TextInst.translatable("nbteditor.hdb.head_not_found"));
						else
							InventoryUtils.purchaseHead(head, 1, "", "");
						return Command.SINGLE_SUCCESS;
					})))
				.then(literal("all").then(argument("category", EnumArgumentType.options(Category.class)).executes(context -> {
					if (!HeadAPI.checkUpdated())
						return Command.SINGLE_SUCCESS;
					Category category = context.getArgument("category", Category.class);
					ItemStack shulker = new ItemStack(getBlock(MainUtil.getDyeColor(category.getColor())));
					shulker.set(DataComponents.CUSTOM_NAME,TextInst.of(ChatFormatting.RESET.toString() + category.getColor() + ChatFormatting.BOLD + category.getTranslatedName().toUpperCase()));
					ItemTagReferences.HIDE_FLAGS.set(shulker, Map.of(HideFlag.CONTAINER, true));
					ContainerIOs.writeRecursively(shulker, HeadAPI.getHeads(category).stream().map(Head::getItemStack).toList());
					MainUtil.getWithMessage(shulker);
					return Command.SINGLE_SUCCESS;
				})).then(literal("search").then(argument("query", StringArgumentType.greedyString()).executes(context -> {
					if (!HeadAPI.checkUpdated())
						return Command.SINGLE_SUCCESS;
					String query = context.getArgument("query", String.class);
					ItemStack shulker = new ItemStack(Items.DYED_SHULKER_BOX.pick(net.minecraft.world.item.DyeColor.BROWN));
					shulker.set(DataComponents.CUSTOM_NAME,TextInst.of(ChatFormatting.RESET.toString() + ChatFormatting.GOLD + ChatFormatting.BOLD + TextInst.translatable("nbteditor.hdb.search").getString() + ": " + query));
					ItemTagReferences.HIDE_FLAGS.set(shulker, Map.of(HideFlag.CONTAINER, true));
					ContainerIOs.writeRecursively(shulker, HeadAPI.getHeadsByName(query).stream().map(Head::getItemStack).toList());
					MainUtil.getWithMessage(shulker);
					return Command.SINGLE_SUCCESS;
				}))))
				.then(literal("update").executes(context -> {
					context.getSource().sendFeedback(TextInst.translatable("nbteditor.hdb.updating_database"));
					Thread thread = new Thread(() -> {
						HeadAPI.updateDatabase();
						context.getSource().sendFeedback(TextInst.translatable("nbteditor.hdb.updated_database"));
					}, "NBTEditor/Async/HeadRefresh/Manual");
					thread.setDaemon(true);
					thread.start();
					return Command.SINGLE_SUCCESS;
				}))
				.executes(context -> {
					if (HeadAPI.checkUpdated())
						HeadAPI.openDatabase();
					return Command.SINGLE_SUCCESS;
				});
	}
	
}
