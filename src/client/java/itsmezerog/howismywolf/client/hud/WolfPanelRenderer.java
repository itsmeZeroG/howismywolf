package itsmezerog.howismywolf.client.hud;

import itsmezerog.howismywolf.client.config.ModConfig;
import itsmezerog.howismywolf.client.hud.style.PanelStyle;
import itsmezerog.howismywolf.client.hud.style.PanelStyles;
import itsmezerog.howismywolf.client.wolf.WolfInfo;
import itsmezerog.howismywolf.client.wolf.WolfTracker;

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

    private static final int SECTION_GAP = 2;
    private static final int OWNER_GAP = 6;

    private static final int SCREEN_MARGIN = 2;
    private static final double ANCHOR_HEIGHT_FRACTION = 0.5; // 0 = feet, 1 = top of the wolf

    private WolfPanelRenderer() {
    }

    public static void render(GuiGraphicsExtractor g, DeltaTracker delta) {
        WolfInfo info = WolfTracker.info();
        Wolf wolf = WolfTracker.wolf();

        if (info == null || wolf == null)
            return;

        Minecraft mc = Minecraft.getInstance();
        Font font = mc.font;

        ModConfig config = ModConfig.get();
        PanelStyle style = PanelStyles.current();
        int padding = style.padding();

        String title = info.name();
        String owner = (config.showOwner && !info.ownerName().isEmpty()) ? "(" + info.ownerName() + ")" : "";
        boolean hasOwner = !owner.isEmpty();
        String state = switch (info.state()) {
            case ANGRY -> "Angry";
            case SITTING -> "Sitting";
            case FOLLOWING -> "Following";
        };

        boolean showHearts = config.showHearts;
        int hp = (int) Math.ceil(info.health());
        int heartCount = (int) Math.ceil(info.maxHealth() / HEALTH_PER_HEART);
        int heartRows = (heartCount + HEARTS_PER_ROW - 1) / HEARTS_PER_ROW;
        int heartsWidth = showHearts
                ? Math.min(heartCount, HEARTS_PER_ROW) * HEART_SPACING + (HEART_SIZE - HEART_SPACING)
                : 0;
        int heartsHeight = showHearts ? heartRows * HEART_ROW_HEIGHT : 0;

        ItemStack armor = info.armor();
        boolean showArmor = config.showArmor && !armor.isEmpty();
        String durability = "";
        if (showArmor && armor.isDamageableItem()) {
            int remaining = armor.getMaxDamage() - armor.getDamageValue();
            durability = remaining + " / " + armor.getMaxDamage();
        }
        int armorRowWidth = showArmor ? ITEM_SIZE + ITEM_TEXT_GAP + font.width(durability) : 0;

        boolean showState = config.showState;
        int titleRowWidth = font.width(title) + (hasOwner ? OWNER_GAP + font.width(owner) : 0);
        int contentWidth = Math.max(titleRowWidth,
                Math.max(armorRowWidth, Math.max(heartsWidth, showState ? font.width(state) : 0)));

        int contentHeight = font.lineHeight;
        if (showHearts)
            contentHeight += SECTION_GAP + heartsHeight;
        if (showArmor)
            contentHeight += SECTION_GAP + ITEM_SIZE;
        if (showState)
            contentHeight += SECTION_GAP + font.lineHeight;

        int boxWidth = contentWidth + padding * 2;
        int boxHeight = contentHeight + padding * 2;

        float partialTick = delta.getGameTimeDeltaPartialTick(false);
        Vec3 anchor = wolf.getPosition(partialTick).add(0, wolf.getBbHeight() * ANCHOR_HEIGHT_FRACTION, 0);
        ScreenProjector.Point p = ScreenProjector.project(anchor);
        if (p == null)
            return;

        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();

        int boxX = Math.round(p.x()) + config.panelOffsetX;
        int boxY = Math.round(p.y()) - boxHeight / 2;
        boxX = Math.max(SCREEN_MARGIN, Math.min(boxX, screenWidth - boxWidth - SCREEN_MARGIN));
        boxY = Math.max(SCREEN_MARGIN, Math.min(boxY, screenHeight - boxHeight - SCREEN_MARGIN));

        style.drawBackground(g, boxX, boxY, boxWidth, boxHeight, info);

        int textX = boxX + padding;
        int y = boxY + padding;

        g.text(font, title, textX, y, style.titleColor(), true);
        if (hasOwner) {
            g.text(font, owner, textX + font.width(title) + OWNER_GAP, y, style.ownerColor(), true);
        }
        y += font.lineHeight;

        if (showHearts) {
            y += SECTION_GAP;
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
            y += heartsHeight;
        }

        if (showArmor) {
            y += SECTION_GAP;
            g.item(armor, textX, y);
            if (!durability.isEmpty()) {
                int textY = y + (ITEM_SIZE - font.lineHeight) / 2;
                g.text(font, durability, textX + ITEM_SIZE + ITEM_TEXT_GAP, textY, style.infoColor(), true);
            }
            y += ITEM_SIZE;
        }

        if (showState) {
            y += SECTION_GAP;
            g.text(font, state, textX, y, style.infoColor(), true);
        }
    }

    private static void drawSprite(GuiGraphicsExtractor g, Identifier sprite, int x, int y) {
        g.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, x, y, HEART_SIZE, HEART_SIZE);
    }
}
