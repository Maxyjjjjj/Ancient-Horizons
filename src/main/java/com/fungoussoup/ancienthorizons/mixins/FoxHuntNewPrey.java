package com.fungoussoup.ancienthorizons.mixins;

import com.fungoussoup.ancienthorizons.registry.ModTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Fox;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Predicate;

@Mixin(Fox.class)
public class FoxHuntNewPrey {

    @Shadow @Final @Mutable
    static Predicate<Entity> STALKABLE_PREY;

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void modifyStalkablePrey(CallbackInfo ci) {
        // Store the original predicate
        Predicate<Entity> originalPredicate = STALKABLE_PREY;

        STALKABLE_PREY = entity -> {
            if (originalPredicate.test(entity)) {
                return true;
            }
            return entity.getType().is(ModTags.EntityTypes.FOX_PREY);
        };
    }

    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void addCustomPreyTargeting(CallbackInfo ci) {
        Fox fox = (Fox) (Object) this;

        // Add targeting goal for our custom prey
        fox.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(
                fox,
                Animal.class,
                10,
                false,
                false,
                entity -> entity.getType().is(ModTags.EntityTypes.FOX_PREY)
        ));
    }
}