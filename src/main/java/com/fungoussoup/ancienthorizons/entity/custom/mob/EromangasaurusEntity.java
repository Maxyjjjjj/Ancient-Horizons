package com.fungoussoup.ancienthorizons.entity.custom.mob;

import com.fungoussoup.ancienthorizons.entity.ModEntities;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.AbstractSchoolingFish;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class EromangasaurusEntity extends AirBreathingWaterAnimal {

    public EromangasaurusEntity(EntityType<? extends EromangasaurusEntity> type, Level level) {
        super(type, level);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new SurfaceForAirGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.5D));
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new FollowParentGoal(this, 1.1D));
        this.goalSelector.addGoal(4, new TemptGoal(this, 1.2D,
                stack -> stack.is(ItemTags.FISHES), false));
        this.goalSelector.addGoal(5, new RandomSwimmingGoal(this, 1.0D, 40));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, AbstractSchoolingFish.class, false));
    }

    /**
     * Define entity attributes
     */
    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 30.0D)  // Decent health for a large aquatic reptile
                .add(Attributes.MOVEMENT_SPEED, 0.25D)  // Moderate swimming speed
                .add(Attributes.ARMOR, 2.0D);  // Some natural armor
    }

    /**
     * Check if the item is food for breeding
     */
    @Override
    public boolean isFood(ItemStack itemStack) {
        return itemStack.is(ItemTags.FISHES);
    }

    /**
     * Create offspring when breeding
     */
    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        return ModEntities.EROMANGASAURUS.get().create(serverLevel);
    }

    /**
     * Customize air supply - Eromangasaurus could hold breath for longer
     */
    @Override
    public int getMaxAirSupply() {
        return 6000; // 5 minutes underwater (300 seconds)
    }

    /**
     * When to start seeking surface - surfaces with 90 seconds of air left
     */
    @Override
    protected int getAirSupplyThreshold() {
        return 1800; // 90 seconds
    }

    // Sound events - replace with your custom sounds if available

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return this.isInWaterOrBubble() ? SoundEvents.DOLPHIN_AMBIENT_WATER : SoundEvents.DOLPHIN_AMBIENT;
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.DOLPHIN_HURT;
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.DOLPHIN_DEATH;
    }

    @Override
    protected SoundEvent getSwimSound() {
        return SoundEvents.DOLPHIN_SWIM;
    }

    @Override
    public int getAmbientSoundInterval() {
        return 200; // Play ambient sound every 10 seconds
    }

    @Override
    protected float getSoundVolume() {
        return 0.6F;
    }

    @Override
    public void push(Entity entity) {
        if (!this.isBaby() && entity instanceof Animal animal && !animal.isBaby()) {
            super.push(entity);
        } else if (!this.isBaby()) {
            entity.push(this);
        }
    }
}