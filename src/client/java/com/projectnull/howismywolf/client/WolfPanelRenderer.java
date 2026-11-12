package com.projectnull.howismywolf.client;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public final class WolfPanelRenderer {
    private static final int PANEL_BACKGROUND = 0xF0100010;
    private static final int PANEL_BORDER = 0xFF3C0C8C;
    private static final int TITLE_COLOR = 0xFFFFFFFF;
    private static final int OWNER_COLOR = 0xFF8FA3B8;
    private static final int INFO_TEXT_COLOR = 0xFFAAAAAA;

    private static final Identifier HEART_CONTAINER = Identifier.withDefaultNamespace("hud/heart/container");
    private static final Identifier HEART_FULL = Identifier.withDefaultNamespace("hud/heart/full");
    private static final Identifier HEART_HALF = Identifier.withDefaultNamespace("hud/heart/half");

    private static final int HEART_SIZE = 9;
    private static final int HEART_SPACING = 8; // less than HEART_SIZE so hearts overlap by 1px, like vanilla
    private static final int HEART_ROW_HEIGHT = HEART_SIZE + 1;
    private static final int HEARTS_PER_ROW = 10;
    private static final int HEALTH_PER_HEART = 2;

    private static final int ITEM_SIZE = 16;
    private static final int ITEM_TEXT_GAP = 4;

    private static final int PANEL_PADDING = 4;
    private static final int LINE_SPACING = 2;
    private static final int SECTION_GAP = 2;
    private static final int OWNER_GAP = 6;

    private static final int PANEL_OFFSET_X = 32;
    private static final int SCREEN_MARGIN = 2;
    private static final double ANCHOR_HEIGHT_FRACTION = 0.5; // 0 = feet, 1 = top of the wolf

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
        boolean hasOwner = !owner.isEmpty();
        String state = switch (info.state()) {
            case ANGRY -> "Angry";
            case SITTING -> "Sitting";
            case FOLLOWING -> "Following";
        };

        int hp = (int) Math.ceil(info.health());
        int heartCount = (int) Math.ceil(info.maxHealth() / HEALTH_PER_HEART);
        int heartRows = (heartCount + HEARTS_PER_ROW - 1) / HEARTS_PER_ROW;
        int heartsWidth = Math.min(heartCount, HEARTS_PER_ROW) * HEART_SPACING + (HEART_SIZE - HEART_SPACING);
        int heartsHeight = heartRows * HEART_ROW_HEIGHT;

        ItemStack armor = info.armor();
        boolean hasArmor = !armor.isEmpty();
        String durability = "";
        if (hasArmor && armor.isDamageableItem()) {
            int remaining = armor.getMaxDamage() - armor.getDamageValue();
            durability = remaining + " / " + armor.getMaxDamage();
        }
        int armorRowWidth = hasArmor ? ITEM_SIZE + ITEM_TEXT_GAP + font.width(durability) : 0;
        int armorRowHeight = hasArmor ? ITEM_SIZE + SECTION_GAP : 0;

        int lineHeight = font.lineHeight + LINE_SPACING;
        int titleRowWidth = font.width(title) + (hasOwner ? OWNER_GAP + font.width(owner) : 0);
        int contentWidth = Math.max(titleRowWidth,
                Math.max(armorRowWidth, Math.max(heartsWidth, font.width(state))));
        int contentHeight = lineHeight + heartsHeight + SECTION_GAP + armorRowHeight + font.lineHeight;

        int boxWidth = contentWidth + PANEL_PADDING * 2;
        int boxHeight = contentHeight + PANEL_PADDING * 2;

        float partialTick = delta.getGameTimeDeltaPartialTick(false);
        Vec3 anchor = wolf.getPosition(partialTick).add(0, wolf.getBbHeight() * ANCHOR_HEIGHT_FRACTION, 0);
        ScreenProjector.Point p = ScreenProjector.project(anchor);
        if (p == null)
            return;

        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();

        int boxX = Math.round(p.x()) + PANEL_OFFSET_X;
        int boxY = Math.round(p.y()) - boxHeight / 2;
        boxX = Math.max(SCREEN_MARGIN, Math.min(boxX, screenWidth - boxWidth - SCREEN_MARGIN));
        boxY = Math.max(SCREEN_MARGIN, Math.min(boxY, screenHeight - boxHeight - SCREEN_MARGIN));

        g.fill(boxX, boxY, boxX + boxWidth, boxY + boxHeight, PANEL_BACKGROUND);
        g.outline(boxX, boxY, boxWidth, boxHeight, PANEL_BORDER);

        int textX = boxX + PANEL_PADDING;
        int y = boxY + PANEL_PADDING;

        g.text(font, title, textX, y, TITLE_COLOR, true);
        if (hasOwner) {
            g.text(font, owner, textX + font.width(title) + OWNER_GAP, y, OWNER_COLOR, true);
        }
        y += lineHeight;

        for (int i = 0; i < heartCount; i++) {
            int hx = textX + (i % HEARTS_PER_ROW) * HEART_SPACING;
            int hy = y + (i / HEARTS_PER_ROW) * HEART_ROW_HEIGHT;
            drawSprite(g, HEART_CONTAINER, hx, hy);
            if (hp >= (i + 1) * HEALTH_PER_HEART) {
                drawSprite(g, HEART_FULL, hx, hy);
            } else if (hp == i * HEALTH_PER_HEART + 1) {
                drawSprite(g, HEART_HALF, hx, hy);
            }
        }
        y += heartsHeight + SECTION_GAP;

        if (hasArmor) {
            g.item(armor, textX, y);
            if (!durability.isEmpty()) {
                int textY = y + (ITEM_SIZE - font.lineHeight) / 2;
                g.text(font, durability, textX + ITEM_SIZE + ITEM_TEXT_GAP, textY, INFO_TEXT_COLOR, true);
            }
            y += armorRowHeight;
        }

        g.text(font, state, textX, y, INFO_TEXT_COLOR, true);
    }

    private static void drawSprite(GuiGraphicsExtractor g, Identifier sprite, int x, int y) {
        g.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, x, y, HEART_SIZE, HEART_SIZE);
    }
}
