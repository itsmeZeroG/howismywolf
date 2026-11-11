package com.projectnull.howismywolf.client;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.phys.Vec3;

public final class WolfPanelRenderer {
    private static final int BACKGROUND = 0xF0100010;
    private static final int BORDER = 0xFF3C0C8C;
    private static final int TITLE = 0xFFFFFFFF;
    private static final int OWNER = 0xFF8FA3B8;
    private static final int LINE = 0xFFAAAAAA;

    private static final int PANEL_EDGE_PADDING = 4;
    private static final int LINE_HEIGHT_PADDING = 2;
    private static final int TITLE_GAP = 6;

    private static final int PANEL_SCREEN_EDGE_PADDING = 2;
    private static final int PANEL_OFFSET_FROM_WOLF = 32;

    private WolfPanelRenderer() {
    }

    public static void render(GuiGraphicsExtractor g, DeltaTracker delta) {
        WolfInfo info = HowIsMyWolfClient.currentWolfInfo();
        Wolf wolf = HowIsMyWolfClient.currentWolf();

        if (info == null || wolf == null)
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

        int lineHeight = font.lineHeight + LINE_HEIGHT_PADDING;
        int contentWidth = Math.max(font.width(title) + TITLE_GAP + font.width(owner),
                Math.max(font.width(health), font.width(state)));
        int contentHeight = lineHeight * 3;

        int boxWidth = contentWidth + PANEL_EDGE_PADDING * 2;
        int boxHeight = contentHeight + PANEL_EDGE_PADDING * 2;

        float partialTick = delta.getGameTimeDeltaPartialTick(false);
        Vec3 anchor = wolf.getPosition(partialTick).add(0, wolf.getBbHeight() * 0.5, 0);
        ScreenProjector.Point p = ScreenProjector.project(anchor);
        if (p == null)
            return;

        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();

        int boxX = Math.round(p.x()) + PANEL_OFFSET_FROM_WOLF;
        int boxY = Math.round(p.y()) - boxHeight / 2;

        boxX = Math.max(PANEL_SCREEN_EDGE_PADDING,
                Math.min(boxX, screenWidth - boxWidth - PANEL_SCREEN_EDGE_PADDING));
        boxY = Math.max(PANEL_SCREEN_EDGE_PADDING,
                Math.min(boxY, screenHeight - boxHeight - PANEL_SCREEN_EDGE_PADDING));

        g.fill(boxX, boxY, boxX + boxWidth, boxY + boxHeight, BACKGROUND);
        g.outline(boxX, boxY, boxWidth, boxHeight, BORDER);

        int textX = boxX + PANEL_EDGE_PADDING;
        int y = boxY + PANEL_EDGE_PADDING;

        g.text(font, title, textX, y, TITLE, true);
        g.text(font, owner, textX + font.width(title + TITLE_GAP), y, OWNER, true);
        y += lineHeight;
        g.text(font, health, textX, y, LINE, true);
        y += lineHeight;
        g.text(font, state, textX, y, LINE, true);
    }
}
