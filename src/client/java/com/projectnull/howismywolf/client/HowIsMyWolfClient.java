package com.projectnull.howismywolf.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.animal.wolf.Wolf;

public class HowIsMyWolfClient implements ClientModInitializer {
	private static WolfInfo currentWolfInfo;

	public static WolfInfo currentWolfInfo() {
		return currentWolfInfo;
	}

	@Override
	public void onInitializeClient() {
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (client.player == null) {
				currentWolfInfo = null;
				return;
			}

			Wolf targetWolf = (client.crosshairPickEntity instanceof Wolf wolf && wolf.isTame()) ? wolf : null;
			currentWolfInfo = targetWolf != null ? WolfInfo.from(targetWolf, client) : null;
		});

		HudElementRegistry.attachElementBefore(VanillaHudElements.CHAT,
				Identifier.fromNamespaceAndPath("howismywolf", "wolf_panel"), WolfPanelRenderer::render);
	}
}
