package com.fungoussoup.ancienthorizons.entity.custom.mob;

import com.fungoussoup.ancienthorizons.entity.ModEntities;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public class ZorseEntity extends AbstractHorse {
    public ZorseEntity(EntityType<? extends AbstractHorse> entityType, Level level) {
        super(entityType, level);
    }

    protected void registerGoals() {
        super.registerGoals();
    }

    protected SoundEvent getAmbientSound() {
        return SoundEvents.MULE_AMBIENT;
    }

    protected SoundEvent getAngrySound() {
        return SoundEvents.MULE_ANGRY;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.MULE_DEATH;
    }

    @Nullable
    protected SoundEvent getEatingSound() {
        return SoundEvents.MULE_EAT;
    }

    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.MULE_HURT;
    }

    protected void playJumpSound() {
        this.playSound(SoundEvents.MULE_JUMP, 0.4F, 1.0F);
    }

    @Nullable
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        return ModEntities.ZORSE.get().create(level);
    }
}
