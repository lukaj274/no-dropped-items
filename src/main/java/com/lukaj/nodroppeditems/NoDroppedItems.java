package com.lukaj.nodroppeditems;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import org.apache.logging.log4j.core.jmx.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NoDroppedItems implements ModInitializer {
	public static final String MOD_ID = "no-dropped-items";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static boolean active = false;

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Hello Fabric world!");

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			dispatcher.register(Commands.literal("ndi").requires(source -> source.hasPermission(2)).executes(context -> {
				active = !active;
				String message = active ? "Now removing dropped items" : "No longer removing dropped items";
				context.getSource().sendSuccess(() -> Component.literal(message), false);
				return 1;
			}));
		});

		ServerTickEvents.END_SERVER_TICK.register(server -> {
			if (active) {
				for (ServerLevel world : server.getAllLevels()) {
					world.getEntities(EntityType.ITEM, item -> true)
							.forEach(ItemEntity::discard);
				}
			}
		});
	}


	public static ResourceLocation id(String path) {
		return new ResourceLocation(MOD_ID, path);
	}
}
