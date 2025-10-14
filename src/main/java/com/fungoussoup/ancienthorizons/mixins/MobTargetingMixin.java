package com.fungoussoup.ancienthorizons.mixins;

import com.fungoussoup.ancienthorizons.entity.custom.mob.MantisEntity;
import com.fungoussoup.ancienthorizons.entity.custom.mob.RuffEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Spider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// Additional mixin for the Mob class to handle target selection more broadly
@Mixin(Mob.class)
abstract class MobTargetingMixin {

    @Inject(method = "setTarget", at = @At("HEAD"), cancellable = true)
    private void preventSpiderTargetingRuffs(LivingEntity target, CallbackInfo ci) {
        Mob mob = (Mob) (Object) this;

        // If this is a spider trying to target a ruff, cancel it
        if (mob instanceof Spider && target instanceof RuffEntity) {
            ci.cancel();
        }
    }

    @Inject(method = "doHurtTarget", at = @At("HEAD"), cancellable = true)
    private void disableAttackWhenGrabbed(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        Mob mob = (Mob) (Object) this;

        if (mob instanceof Spider spider) {
            if (entity instanceof MantisEntity mantis && mantis.hasPrey() && mantis.getHeldPrey() == spider) {
                cir.cancel();
            }
        }
    }
}
