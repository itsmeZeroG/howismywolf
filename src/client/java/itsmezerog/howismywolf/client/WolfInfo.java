package itsmezerog.howismywolf.client;

import itsmezerog.howismywolf.HowIsMyWolf;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;

public record WolfInfo(
        String name,
        String ownerName,
        float health,
        float maxHealth,
        DyeColor collarColor,
        ItemStack armor,
        WolfState state) {
    public enum WolfState {
        ANGRY, SITTING, FOLLOWING
    }

    public static WolfInfo from(Wolf wolf, Minecraft mc) {
        WolfState state = wolf.isAngry() ? WolfState.ANGRY
                : wolf.isInSittingPose() ? WolfState.SITTING : WolfState.FOLLOWING;

        return new WolfInfo(wolf.getDisplayName().getString(), resolveOwnerName(wolf, mc), wolf.getHealth(),
                wolf.getMaxHealth(), wolf.getCollarColor(), wolf.getBodyArmorItem(), state);
    }

    private static String resolveOwnerName(Wolf wolf, Minecraft mc) {
        LivingEntity owner = wolf.getOwner();

        if (owner != null)
            return owner.getName().getString();

        return "";
    }
}
