package com.projectnull.howismywolf.client;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public final class WolfPanelRenderer {
    private static final int BACKGROUND = 0xF0100010;
    private static final int BORDER = 0xFF3C0C8C;
    private static final int TITLE = 0xFFFFFFFF;
    private static final int OWNER = 0xFF8FA3B8;
    private static final int LINE = 0xFFAAAAAA;

    private static final int PADDING = 4;
    private static final int TITLE_GAP = 6;

    private WolfPanelRenderer() {
    }

    public static void render(GuiGraphicsExtractor g, DeltaTracker delta) {
        WolfInfo info = HowIsMyWolfClient.currentWolfInfo();
        if (info == null)
            return;

        Minecraft mc = Minecraft.getInstance();
        Font font = mc.font;

        String title = info.name();
        String owner = info.ownerName().isEmpty() ? "" : "(" + info.ownerName() + ")";
        String health = String.format("Health: %.1f / %.1f", info.health(), info.maxHealth());
        String state = "State: " + switch (info.state()) {
            case ANGRY -> "Angry";
            case SITTING -> "Sitting";
            case FOLLOWING -> "Following";
        };

        int lineHeight = font.lineHeight + 2;
        int contentWidth = Math.max(font.width(title) + TITLE_GAP + font.width(owner),
                Math.max(font.width(health), font.width(state)));
        int contentHeight = lineHeight * 3;

        int boxWidth = contentWidth + PADDING * 2;
        int boxHeight = contentHeight + PADDING * 2;

        int boxX = mc.getWindow().getGuiScaledWidth() / 2 + 14;
        int boxY = mc.getWindow().getGuiScaledHeight() / 2 - boxHeight / 2;

        g.fill(boxX, boxY, boxX + boxWidth, boxY + boxHeight, BACKGROUND);
        g.outline(boxX, boxY, boxWidth, boxHeight, BORDER);

        int textX = boxX + PADDING;
        int y = boxY + PADDING;

        g.text(font, title, textX, y, TITLE, true);
        g.text(font, owner, textX + font.width(title + TITLE_GAP), y, OWNER, true);
        y += lineHeight;
        g.text(font, health, textX, y, LINE, true);
        y += lineHeight;
        g.text(font, state, textX, y, LINE, true);
    }
}
