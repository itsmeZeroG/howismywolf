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

			Wolf targetWolf = null;
			if (client.crosshairPickEntity instanceof Wolf wolf && wolf.isTame()) {
				targetWolf = wolf;
			}

			if (targetWolf != lastWolf) {
				lastWolf = targetWolf;

				if (targetWolf != null) {
					HowIsMyWolf.LOGGER.info("Loooking at {} - {}/{} HP", targetWolf.getDisplayName().getString(),
							targetWolf.getHealth(), targetWolf.getMaxHealth());
				}
			}
		});
	}
}
