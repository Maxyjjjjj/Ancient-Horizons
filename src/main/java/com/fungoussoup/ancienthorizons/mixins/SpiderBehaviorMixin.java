package com.fungoussoup.ancienthorizons.mixins;

import com.fungoussoup.ancienthorizons.entity.custom.mob.MantisEntity;
import com.fungoussoup.ancienthorizons.entity.custom.mob.RuffEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Spider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Spider.class)
public class SpiderBehaviorMixin {

    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void addCustomGoals(CallbackInfo ci) {
        Spider spider = (Spider) (Object) this;

        // Flee from ruffs
        spider.goalSelector.addGoal(1, new AvoidEntityGoal<>(spider, RuffEntity.class,
                8.0F, 1.8D, 1.4D,
                livingEntity -> livingEntity instanceof RuffEntity && livingEntity.isAlive()));
    }
}
