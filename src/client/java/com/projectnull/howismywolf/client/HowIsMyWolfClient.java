package com.projectnull.howismywolf.client;

import com.projectnull.howismywolf.HowIsMyWolf;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.world.entity.animal.wolf.Wolf;

public class HowIsMyWolfClient implements ClientModInitializer {
	private static Wolf lastWolf;

	@Override
	public void onInitializeClient() {
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (client.player == null)
				return;

			Wolf targetWolf = (client.crosshairPickEntity instanceof Wolf wolf && wolf.isTame()) ? wolf : null;

			if (lastWolf == targetWolf)
				return;
			lastWolf = targetWolf;

			if (targetWolf == null)
				return;

			WolfInfo info = WolfInfo.from(targetWolf, client);
			HowIsMyWolf.LOGGER.info("{}", info);
		});
	}
}
