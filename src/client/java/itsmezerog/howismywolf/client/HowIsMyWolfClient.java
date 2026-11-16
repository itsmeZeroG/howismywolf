package itsmezerog.howismywolf.client;

import itsmezerog.howismywolf.client.config.ModConfig;
import itsmezerog.howismywolf.client.hud.WolfPanelRenderer;
import itsmezerog.howismywolf.client.wolf.WolfTracker;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.resources.Identifier;

public class HowIsMyWolfClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ModConfig.load();
        WolfTracker.init();
        HudElementRegistry.attachElementBefore(VanillaHudElements.CHAT,
                Identifier.fromNamespaceAndPath("howismywolf", "wolf_panel"), WolfPanelRenderer::render);
    }
}
