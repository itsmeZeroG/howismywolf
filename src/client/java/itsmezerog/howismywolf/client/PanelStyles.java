package itsmezerog.howismywolf.client;

public final class PanelStyles {
    private static final PanelStyle VANILLA = new VanillaPanelStyle();
    private static final PanelStyle DYE_BORDER = new DyeBorderPanelStyle();

    private PanelStyles() {
    }

    public static PanelStyle current() {
        return switch (HowIsMyWolfConfig.get().style) {
            case VANILLA -> VANILLA;
            case DYE_BORDER -> DYE_BORDER;
        };
    }
}
