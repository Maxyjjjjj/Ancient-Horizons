package com.fungoussoup.ancienthorizons.mixins;

import com.fungoussoup.ancienthorizons.registry.ModTags;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.entity.animal.PolarBear;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PolarBear.class)
public class PolarBearMixin {

    @Inject(method = "registerGoals", at = @At("HEAD"))
    private void registerGoals(CallbackInfo ci) {
        PolarBear polarBear = (PolarBear) (Object) this;
        polarBear.goalSelector.addGoal(3, new TemptGoal(polarBear, 1.25D, Ingredient.of(Items.COD, Items.SALMON), false));
        polarBear.goalSelector.addGoal(4, new BreedGoal(polarBear, 1.0D));
        // Replace default fox targeting with mod-defined prey tag
        polarBear.targetSelector.removeGoal(new NearestAttackableTargetGoal<>(polarBear, Fox.class, true));
        polarBear.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(polarBear, Animal.class, false,
                entity -> entity.getType().is(ModTags.EntityTypes.POLAR_BEAR_PREY)) {
            @Override
            public boolean canUse() {
                return !polarBear.isBaby() && super.canUse();
            }
        });
    }

    /**
     * Makes polar bears breedable with raw fish (cod/salmon).
     */
    @Inject(method = "isFood", at = @At("HEAD"), cancellable = true)
    private void isFood(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (stack.is(Items.SALMON) || stack.is(Items.COD)) {
            cir.setReturnValue(true);
        }
    }
}

