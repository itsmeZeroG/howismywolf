package itsmezerog.howismywolf.client.hud.style;

import itsmezerog.howismywolf.client.config.ModConfig;

public final class PanelStyles {
    private static final PanelStyle VANILLA = new VanillaPanelStyle();
    private static final PanelStyle DYE_BORDER = new DyeBorderPanelStyle();

    private PanelStyles() {
    }

    public static PanelStyle current() {
        return switch (ModConfig.get().style) {
            case VANILLA -> VANILLA;
            case DYE_BORDER -> DYE_BORDER;
        };
    }
}
