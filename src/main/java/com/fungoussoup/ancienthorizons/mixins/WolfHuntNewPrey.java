package com.fungoussoup.ancienthorizons.mixins;

import com.fungoussoup.ancienthorizons.mixins.accessor.NearestAttackableTargetGoalAccessor;
import com.fungoussoup.ancienthorizons.registry.ModTags;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NonTameRandomTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.entity.animal.Wolf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.minecraft.world.entity.animal.Wolf.PREY_SELECTOR;

@Mixin(Wolf.class)
public class WolfHuntNewPrey {
    @Inject(method = "registerGoals", at = @At("HEAD"))
    private void registerGoals(CallbackInfo ci) {
        Wolf wolf = (Wolf) (Object) this;

        // Remove any NonTameRandomTargetGoal (safer than trying to remove by instance)
        wolf.targetSelector.getAvailableGoals().removeIf(
                wrapped -> wrapped.getGoal() instanceof NonTameRandomTargetGoal
        );

        // Add your custom prey-targeting goal
        wolf.targetSelector.addGoal(1, new NonTameRandomTargetGoal<>(
                wolf,
                Animal.class,
                false,
                entity -> entity.getType().is(ModTags.EntityTypes.WOLF_PREY)
        ) {
            @Override
            public boolean canUse() {
                return !wolf.isBaby() && super.canUse();
            }
        });
    }
}