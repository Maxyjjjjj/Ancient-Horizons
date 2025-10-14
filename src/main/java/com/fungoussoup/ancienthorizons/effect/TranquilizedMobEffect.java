package com.fungoussoup.ancienthorizons.effect;

import com.fungoussoup.ancienthorizons.registry.ModEffects;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.util.Mth;

public class TranquilizedMobEffect extends MobEffect {

    public TranquilizedMobEffect() {
        super(MobEffectCategory.HARMFUL, 0x89CFF0); // Light blue
    }

    // New custom method to replicate the logic that should happen when the effect is applied
    // This is called by MobEffectInstance when it is first added to an entity.
    @Override
    public void onEffectStarted(LivingEntity entity, int amplifier) {
        if (!entity.level().isClientSide) {
            // 1. Halt AI for Mob entities
            if (entity instanceof Mob mob) {
                mob.setNoAi(true);
            }

            // 2. Rotate the entity 90 degrees to the left (server-side only)
            float newYaw = entity.getYRot() + 90.0F;
            newYaw = Mth.wrapDegrees(newYaw);

            entity.setYRot(newYaw);
            entity.setYHeadRot(newYaw);
        }
    }

    // The method called every tick if shouldApplyEffectTickThisTick returns true.
    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        // We only need this logic for Mobs and only on the server.
        if (!entity.level().isClientSide && entity instanceof Mob mob) {
            // We use the effect's instance to check the duration
            if (entity.hasEffect((ModEffects.TRANQUILIZED))) {
                // If the duration is 1 (meaning this is the last tick before expiration)
                if (entity.getEffect(ModEffects.TRANQUILIZED).getDuration() == 1) {
                    // Restore AI on the very last tick
                    mob.setNoAi(false);
                    return false; // Stop further processing after this
                }
            }
        }
        return false; // Do nothing on other ticks
    }
}