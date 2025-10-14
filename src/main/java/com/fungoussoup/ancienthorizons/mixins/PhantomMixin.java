package com.fungoussoup.ancienthorizons.mixins;

import com.fungoussoup.ancienthorizons.entity.custom.mob.LionEntity;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Phantom;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Phantom.class)
public abstract class PhantomMixin {

    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void ancientHorizons$addLionAttackGoal(CallbackInfo ci) {
        Phantom phantom = (Phantom) (Object) this;

        phantom.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(
                phantom,
                LionEntity.class,
                true,
                false
        ));
    }
}

