package com.fungoussoup.ancienthorizons.entity.custom.mob;

import com.fungoussoup.ancienthorizons.entity.ModEntities;
import com.fungoussoup.ancienthorizons.entity.custom.mob.misc.SemiAquaticAnimal;
import com.fungoussoup.ancienthorizons.registry.ModSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class HippopotamusEntity extends SemiAquaticAnimal implements NeutralMob {
    private static final EntityDataAccessor<Integer> DATA_REMAINING_ANGER_TIME =
            SynchedEntityData.defineId(HippopotamusEntity.class, EntityDataSerializers.INT);
    private static final UniformInt PERSISTENT_ANGER_TIME = TimeUtil.rangeOfSeconds(20, 39);

    private int timeInWater = 0;
    private int timeOnLand = 0;
    private static final int PREFERRED_WATER_TIME = 6000; // 5 minutes
    private static final int PREFERRED_LAND_TIME = 3600; // 3 minutes

    @Nullable
    private UUID persistentAngerTarget;

    public HippopotamusEntity(EntityType<? extends HippopotamusEntity> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 60.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.2D)
                .add(Attributes.ATTACK_DAMAGE, 8.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.8D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.2D, true));
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new TemptGoal(this, 1.1D, Ingredient.of(Items.MELON_SLICE, Items.MELON), false));
        this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.1D));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(
                this,
                LivingEntity.class,
                10,
                true,
                false,
                entity -> entity.getType() == ModEntities.CROCODILE.get() && !this.isNamedGloria()
        ));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 10, true, false, this::isAngryAt));
        this.targetSelector.addGoal(3, new ResetUniversalAngerTargetGoal<>(this, false));
    }

    private boolean isNamedGloria() {
        return this.hasCustomName() && "Gloria".equalsIgnoreCase(this.getName().getString());
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_REMAINING_ANGER_TIME, 0);
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide) {
            if (this.isInWater()) {
                timeInWater++;
                timeOnLand = 0;
            } else {
                timeOnLand++;
                timeInWater = 0;
            }
        }
    }

    @Override
    public boolean shouldEnterWater() {
        // Enter water if been on land too long, or during day (hippos rest in water during day)
        return timeOnLand > PREFERRED_LAND_TIME ||
                (this.level().isDay() && !this.isInWater() && this.random.nextInt(100) < 5);
    }

    @Override
    public boolean shouldLeaveWater() {
        // Leave water at night to graze, or if been in water too long
        return timeInWater > PREFERRED_WATER_TIME ||
                (!this.level().isDay() && this.isInWater() && this.random.nextInt(100) < 3);
    }

    @Override
    public boolean shouldStopMoving() {
        // Stop moving when resting in water during the day
        return this.isInWater() && this.level().isDay() && this.random.nextInt(100) < 80;
    }

    @Override
    public int getWaterSearchRange() {
        return 16;
    }

    @Override
    public boolean isFood(ItemStack itemStack) {
        return itemStack.is(Items.MELON_SLICE) ||
                itemStack.is(Items.WHEAT) ||
                itemStack.is(Items.APPLE);
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        return ModEntities.HIPPOPOTAMUS.get().create(serverLevel);
    }

    // NeutralMob implementation
    @Override
    public int getRemainingPersistentAngerTime() {
        return this.entityData.get(DATA_REMAINING_ANGER_TIME);
    }

    @Override
    public void setRemainingPersistentAngerTime(int time) {
        this.entityData.set(DATA_REMAINING_ANGER_TIME, time);
    }

    @Override
    public @Nullable UUID getPersistentAngerTarget() {
        return this.persistentAngerTarget;
    }

    @Override
    public void setPersistentAngerTarget(@Nullable UUID target) {
        this.persistentAngerTarget = target;
    }

    @Override
    public void startPersistentAngerTimer() {
        this.setRemainingPersistentAngerTime(PERSISTENT_ANGER_TIME.sample(this.random));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        this.addPersistentAngerSaveData(tag);
        tag.putInt("TimeInWater", this.timeInWater);
        tag.putInt("TimeOnLand", this.timeOnLand);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.readPersistentAngerSaveData(this.level(), tag);
        this.timeInWater = tag.getInt("TimeInWater");
        this.timeOnLand = tag.getInt("TimeOnLand");
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide) {
            this.updatePersistentAnger((ServerLevel) this.level(), true);
        }
    }

    // Sounds
    @Override
    protected SoundEvent getAmbientSound() {
        return ModSoundEvents.HIPPOPOTAMUS_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSoundEvents.HIPPOPOTAMUS_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSoundEvents.HIPPOPOTAMUS_DEATH;
    }

    @Override
    public int getAmbientSoundInterval() {
        return 200; // Play ambient sound every 10 seconds
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.POLAR_BEAR_STEP, 0.15F, 1.0F);
    }

    // Combat behavior
    @Override
    public boolean doHurtTarget(Entity target) {
        boolean flag = target.hurt(this.damageSources().mobAttack(this),
                (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE));

        if (flag) {
            double knockbackStrength = 0.5D;
            double dx = target.getX() - this.getX();
            double dz = target.getZ() - this.getZ();
            target.push(dx * knockbackStrength, 0.2D, dz * knockbackStrength);
        }

        return flag;
    }
}