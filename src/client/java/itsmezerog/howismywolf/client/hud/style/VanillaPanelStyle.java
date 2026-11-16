package itsmezerog.howismywolf.client.hud.style;

import itsmezerog.howismywolf.client.wolf.WolfInfo;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.TooltipRenderUtil;

public final class VanillaPanelStyle implements PanelStyle {
    private static final int PADDING = 3;

    @Override
    public int padding() {
        return PADDING;
    }

    @Override
    public int titleColor() {
        return 0xFFFFFFFF;
    }

    @Override
    public int ownerColor() {
        return 0xFF555555;
    }

    @Override
    public int infoColor() {
        return 0xFFAAAAAA;
    }

    @Override
    public void drawBackground(GuiGraphicsExtractor g, int x, int y, int width, int height, WolfInfo info) {
        TooltipRenderUtil.extractTooltipBackground(g, x + PADDING, y + PADDING, width - PADDING * 2,
                height - PADDING * 2, null);
    }
}
