package itsmezerog.howismywolf.client.config;

import java.util.Locale;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class ConfigScreen {
        private ConfigScreen() {
        }

        public static Screen create(Screen parent) {
                ModConfig config = ModConfig.get();

                ConfigBuilder builder = ConfigBuilder.create()
                                .setParentScreen(parent)
                                .setTitle(Component.translatable("config.howismywolf.title"))
                                .setSavingRunnable(ModConfig::save);

                ConfigEntryBuilder entries = builder.entryBuilder();
                ConfigCategory general = builder
                                .getOrCreateCategory(Component.translatable("config.howismywolf.category.general"));

                general.addEntry(entries
                                .startBooleanToggle(Component.translatable("config.howismywolf.show_owner"),
                                                config.showOwner)
                                .setDefaultValue(true)
                                .setSaveConsumer(value -> config.showOwner = value)
                                .build());

                general.addEntry(entries
                                .startBooleanToggle(Component.translatable("config.howismywolf.show_hearts"),
                                                config.showHearts)
                                .setDefaultValue(true)
                                .setSaveConsumer(value -> config.showHearts = value)
                                .build());

                general.addEntry(entries
                                .startBooleanToggle(Component.translatable("config.howismywolf.show_armor"),
                                                config.showArmor)
                                .setDefaultValue(true)
                                .setSaveConsumer(value -> config.showArmor = value)
                                .build());

                general.addEntry(entries
                                .startBooleanToggle(Component.translatable("config.howismywolf.show_state"),
                                                config.showState)
                                .setDefaultValue(true)
                                .setSaveConsumer(value -> config.showState = value)
                                .build());

                general.addEntry(entries
                                .startIntSlider(Component.translatable("config.howismywolf.linger"),
                                                config.lingerTicks, 0, 100)
                                .setDefaultValue(15)
                                .setTextGetter(ticks -> Component
                                                .literal(String.format(Locale.ROOT, "%.2f s", ticks / 20.0f)))
                                .setTooltip(Component.translatable("config.howismywolf.linger.tooltip"))
                                .setSaveConsumer(value -> config.lingerTicks = value)
                                .build());

                general.addEntry(entries
                                .startIntSlider(Component.translatable("config.howismywolf.panel_offset_x"),
                                                config.panelOffsetX, 0,
                                                128)
                                .setDefaultValue(32)
                                .setTooltip(Component.translatable("config.howismywolf.panel_offset_x.tooltip"))
                                .setSaveConsumer(value -> config.panelOffsetX = value)
                                .build());

                general.addEntry(entries
                                .startEnumSelector(Component.translatable("config.howismywolf.style"),
                                                ModConfig.StyleType.class, config.style)
                                .setDefaultValue(ModConfig.StyleType.DYE_BORDER)
                                .setEnumNameProvider(value -> Component.translatable(
                                                "config.howismywolf.style." + value.name().toLowerCase(Locale.ROOT)))
                                .setSaveConsumer(value -> config.style = value)
                                .build());

                return builder.build();
        }
}
