package itsmezerog.howismywolf.client;

import net.minecraft.client.gui.GuiGraphicsExtractor;

public final class DyeBorderPanelStyle implements PanelStyle {
    private static final int PADDING = 3;
    private static final int BACKGROUND = 0xF0100010;
    private static final int BORDER_ALPHA = 0xE0;
    private static final int MIN_CHANNEL = 0x40;
    private static final float BOTTOM_DARKEN = 0.6f;

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
        int rgb = ensureVisible(info.collarColor().getTextureDiffuseColor() & 0xFFFFFF);
        int top = argb(BORDER_ALPHA, rgb);
        int bottom = argb(BORDER_ALPHA, scale(rgb, BOTTOM_DARKEN));

        int right = x + width;
        int bottomEdge = y + height;

        g.fill(x, y - 1, right, y, BACKGROUND);
        g.fill(x, bottomEdge, right, bottomEdge + 1, BACKGROUND);
        g.fill(x, y, right, bottomEdge, BACKGROUND);
        g.fill(x - 1, y, x, bottomEdge, BACKGROUND);
        g.fill(right, y, right + 1, bottomEdge, BACKGROUND);

        g.fillGradient(x, y + 1, x + 1, bottomEdge - 1, top, bottom);
        g.fillGradient(right - 1, y + 1, right, bottomEdge - 1, top, bottom);
        g.fill(x, y, right, y + 1, top);
        g.fill(x, bottomEdge - 1, right, bottomEdge, bottom);
    }

    private static int argb(int alpha, int rgb) {
        return (alpha << 24) | rgb;
    }

    private static int scale(int rgb, float factor) {
        int r = (int) (((rgb >> 16) & 0xFF) * factor);
        int g = (int) (((rgb >> 8) & 0xFF) * factor);
        int b = (int) ((rgb & 0xFF) * factor);
        return (r << 16) | (g << 8) | b;
    }

    private static int ensureVisible(int rgb) {
        int r = Math.max((rgb >> 16) & 0xFF, MIN_CHANNEL);
        int g = Math.max((rgb >> 8) & 0xFF, MIN_CHANNEL);
        int b = Math.max(rgb & 0xFF, MIN_CHANNEL);
        return (r << 16) | (g << 8) | b;
    }
}
