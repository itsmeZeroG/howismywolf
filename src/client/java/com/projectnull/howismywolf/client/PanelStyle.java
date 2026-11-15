package com.projectnull.howismywolf.client;

import net.minecraft.client.gui.GuiGraphicsExtractor;

public interface PanelStyle {
    int padding();

    int titleColor();

    int ownerColor();

    int infoColor();

    void drawBackground(GuiGraphicsExtractor g, int x, int y, int width, int height);
}
