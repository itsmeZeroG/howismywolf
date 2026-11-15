package com.projectnull.howismywolf.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.animal.wolf.Wolf;

public class HowIsMyWolfClient implements ClientModInitializer {
	private static WolfInfo currentWolfInfo;
	private static Wolf currentWolf;

	private static int lingerTicksLeft;

	public static WolfInfo currentWolfInfo() {
		return currentWolfInfo;
	}

	public static Wolf currentWolf() {
		return currentWolf;
	}

	@Override
	public void onInitializeClient() {
		HowIsMyWolfConfig.load();

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (client.player == null || client.level == null) {
				currentWolfInfo = null;
				currentWolf = null;
				lingerTicksLeft = 0;
				return;
			}

			boolean looking = client.crosshairPickEntity instanceof Wolf wolf && wolf.isTame();

			if (looking) {
				currentWolf = (Wolf) client.crosshairPickEntity;
				lingerTicksLeft = HowIsMyWolfConfig.get().lingerTicks;
			} else if (lingerTicksLeft > 0) {
				lingerTicksLeft--;
			}

			if (currentWolf != null && ((!looking && lingerTicksLeft == 0) || currentWolf.isRemoved())) {
				currentWolf = null;
			}

			currentWolfInfo = currentWolf != null ? WolfInfo.from(currentWolf, client) : null;
		});

		HudElementRegistry.attachElementBefore(VanillaHudElements.CHAT,
				Identifier.fromNamespaceAndPath("howismywolf", "wolf_panel"), WolfPanelRenderer::render);
	}
}
