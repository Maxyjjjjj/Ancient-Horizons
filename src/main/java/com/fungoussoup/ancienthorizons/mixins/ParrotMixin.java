package com.fungoussoup.ancienthorizons.mixins;

import net.minecraft.Util;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.entity.animal.ShoulderRidingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Parrot.class)
public class ParrotMixin extends ShoulderRidingEntity {
    protected ParrotMixin(EntityType<? extends ShoulderRidingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void addBreedGoal(CallbackInfo ci) {
        this.goalSelector.addGoal(3, new BreedGoal(this, 1.0D));
    }

    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob other) {
        Parrot baby = EntityType.PARROT.create(level);
        if (baby != null && this.random.nextBoolean()) {
            baby.setVariant(Util.getRandom(Parrot.Variant.values(), level.getRandom()));
            Parrot self = (Parrot) (Object) this;
            self.setVariant(Util.getRandom(Parrot.Variant.values(), level.getRandom()));
        }
        return baby;
    }

    @Override
    public float getAgeScale() {
        return super.getAgeScale();
    }

    @Override
    public boolean isFood(ItemStack itemStack) {
        return itemStack.is(ItemTags.PARROT_FOOD);
    }
}
