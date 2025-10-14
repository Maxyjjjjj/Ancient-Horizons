package com.fungoussoup.ancienthorizons.mixins.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;

@Mixin(NearestAttackableTargetGoal.class)
public interface NearestAttackableTargetGoalAccessor {
    @Accessor("targetType")
    Class<?> getTargetType();
}

