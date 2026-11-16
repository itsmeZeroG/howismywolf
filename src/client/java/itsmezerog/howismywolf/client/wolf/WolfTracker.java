package itsmezerog.howismywolf.client.wolf;

import itsmezerog.howismywolf.client.config.ModConfig;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.animal.wolf.Wolf;

public final class WolfTracker {
    private static Wolf currentWolf;
    private static WolfInfo currentInfo;
    private static int lingerTicksLeft;

    private WolfTracker() {
    }

    public static void init() {
        ClientTickEvents.END_CLIENT_TICK.register(WolfTracker::tick);
    }

    public static Wolf wolf() {
        return currentWolf;
    }

    public static WolfInfo info() {
        return currentInfo;
    }

    private static void tick(Minecraft client) {
        if (client.player == null || client.level == null) {
            currentWolf = null;
            currentInfo = null;
            lingerTicksLeft = 0;
            return;
        }

        Wolf looked = client.crosshairPickEntity instanceof Wolf w && w.isTame() ? w : null;
        if (looked != null) {
            currentWolf = looked;
            lingerTicksLeft = ModConfig.get().lingerTicks;
        } else if (lingerTicksLeft > 0) {
            lingerTicksLeft--;
        }

        boolean expired = looked == null && lingerTicksLeft == 0;
        if (currentWolf != null && (expired || currentWolf.isRemoved())) {
            currentWolf = null;
        }

        currentInfo = currentWolf != null ? WolfInfo.from(currentWolf, client) : null;
    }
}
