package com.projectnull.howismywolf.client;

public final class PanelStyles {
    private static final PanelStyle VANILLA = new VanillaPanelStyle();

    private PanelStyles() {
    }

    public static PanelStyle current() {
        return switch (HowIsMyWolfConfig.get().style) {
            case VANILLA -> VANILLA;
        };
    }
}
