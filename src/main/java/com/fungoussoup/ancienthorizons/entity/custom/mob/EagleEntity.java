package com.fungoussoup.ancienthorizons.entity.custom.mob;

import com.fungoussoup.ancienthorizons.registry.ModEntities;
import com.fungoussoup.ancienthorizons.registry.ModSoundEvents;
import com.fungoussoup.ancienthorizons.registry.ModTags;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class EagleEntity extends AbstractEagleEntity {

    public EagleEntity(EntityType<? extends AbstractEagleEntity> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 24.0)
                .add(Attributes.FLYING_SPEED, 0.8)
                .add(Attributes.MOVEMENT_SPEED, 0.25)
                .add(Attributes.ATTACK_DAMAGE, 6.0)
                .add(Attributes.ARMOR, 2.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.3)
                .add(Attributes.FOLLOW_RANGE, 32.0);
    }

    @Override
    public boolean isValidPrey(LivingEntity entity) {
        return entity.getType().is(ModTags.EntityTypes.EAGLE_PREY);
    }

    @Override
    public int getSwoopCooldownTicks() {
        return 120; // 6 seconds
    }

    @Override
    public double getSwoopSpeed() {
        return this.getAttributeValue(Attributes.FLYING_SPEED);
    }

    @Override
    protected boolean canCarryPrey(LivingEntity prey) {
        return prey.isAlive() && !prey.isRemoved() && isValidPrey(prey);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSoundEvents.EAGLE_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ModSoundEvents.EAGLE_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSoundEvents.EAGLE_DEATH;
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource damageSource) {
        return false;
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        EagleEntity baby = ModEntities.EAGLE.get().create(serverLevel);
        if (baby != null) {
            UUID ownerUUID = this.getOwnerUUID();
            if (ownerUUID != null) {
                baby.setOwnerUUID(ownerUUID);
                baby.setTame(true, true);
            }
        }
        return baby;
    }
}