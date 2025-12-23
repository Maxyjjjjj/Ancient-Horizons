package com.fungoussoup.ancienthorizons.mixins;

import com.fungoussoup.ancienthorizons.entity.custom.mob.HoatzinEntity;
import com.fungoussoup.ancienthorizons.entity.custom.mob.LionEntity;
import net.minecraft.world.entity.ai.goal.target.*;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.world.entity.ai.goal.*;
import com.fungoussoup.ancienthorizons.entity.custom.mob.SnowLeopardEntity;
import com.fungoussoup.ancienthorizons.entity.custom.mob.TigerEntity;

@Mixin(Creeper.class)
public class CreeperMixin {

    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void addAvoidanceGoals(CallbackInfo ci) {
        Creeper creeper = (Creeper) (Object) this;

        // Add avoidance for snow leopards, lions, leopards, tigers and other felids
        creeper.goalSelector.addGoal(3, new AvoidEntityGoal<>(
                creeper,
                SnowLeopardEntity.class,
                8.0F,
                1.0D,
                1.2D
        ));

        creeper.goalSelector.addGoal(3, new AvoidEntityGoal<>(
                creeper,
                TigerEntity.class,
                10.0F,
                1.0D,
                1.2D
        ));

        creeper.goalSelector.addGoal(3, new AvoidEntityGoal<>(
                creeper,
                LionEntity.class,
                10.0F,
                1.0D,
                1.2D
        ));

        creeper.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(
                creeper,
                HoatzinEntity.class,
                false
        ));
    }

    // Prevent creepers from exploding near tigers
    @Inject(method = "explodeCreeper", at = @At("HEAD"), cancellable = true)
    private void preventTigerExplosion(CallbackInfo ci) {
        Creeper creeper = (Creeper) (Object) this;

        // Check if there's a tiger nearby
        boolean tigerNearby = !creeper.level().getEntitiesOfClass(TigerEntity.class,
                creeper.getBoundingBox().inflate(10.0D)).isEmpty();

        boolean lionNearby = !creeper.level().getEntitiesOfClass(LionEntity.class,
                creeper.getBoundingBox().inflate(10.0D)).isEmpty();

        if (tigerNearby || lionNearby) {
            ci.cancel(); // Cancel the explosion
        }
    }

    // Prevent creepers from targeting tigers
    @Inject(method = "setTarget", at = @At("HEAD"), cancellable = true)
    private void preventTargetingTigers(LivingEntity target, CallbackInfo ci) {
        if (target instanceof TigerEntity) {
            ci.cancel(); // Don't set tigers as targets
        }
    }
}