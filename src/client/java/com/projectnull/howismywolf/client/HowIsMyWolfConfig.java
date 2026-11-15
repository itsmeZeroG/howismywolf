package com.projectnull.howismywolf.client;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.projectnull.howismywolf.HowIsMyWolf;

import net.fabricmc.loader.api.FabricLoader;

public final class HowIsMyWolfConfig {
    public enum StyleType {
        VANILLA
    };

    public boolean showOwner = true;
    public boolean showHearts = true;
    public boolean showArmor = true;
    public boolean showState = true;
    public float lingerSeconds = 0.75f;
    public int panelOffsetX = 32;
    public StyleType style = StyleType.VANILLA;

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve("how-is-my-wolf.json");
    private static HowIsMyWolfConfig instance = new HowIsMyWolfConfig();

    public static HowIsMyWolfConfig get() {
        return instance;
    }

    public int lingerTicks() {
        return Math.round(lingerSeconds * 20);
    }

    public static void load() {
        if (Files.exists(PATH)) {
            try (Reader reader = Files.newBufferedReader(PATH)) {
                HowIsMyWolfConfig loaded = GSON.fromJson(reader, HowIsMyWolfConfig.class);
                if (loaded != null) {
                    instance = loaded;
                }
            } catch (IOException | JsonParseException e) {
                HowIsMyWolf.LOGGER.warn("Could not read config, using default config.", e);
            }
        }

        instance.sanitize();
        save();
    }

    public static void save() {
        try (Writer writer = Files.newBufferedWriter(PATH)) {
            GSON.toJson(instance, writer);
        } catch (IOException e) {
            HowIsMyWolf.LOGGER.warn("Could not save config.", e);
        }
    }

    private void sanitize() {
        lingerSeconds = Math.clamp(lingerSeconds, 0.0f, 10.0f);
        panelOffsetX = Math.clamp(panelOffsetX, 0, 128);

        if (style == null) {
            style = StyleType.VANILLA;
        }
    }
}
