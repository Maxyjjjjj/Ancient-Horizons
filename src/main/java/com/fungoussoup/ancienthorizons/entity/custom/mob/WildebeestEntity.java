package com.fungoussoup.ancienthorizons.entity.custom.mob;

import com.fungoussoup.ancienthorizons.registry.ModEntities;
import com.fungoussoup.ancienthorizons.entity.ai.StampedeGoal;
import com.fungoussoup.ancienthorizons.entity.interfaces.Stampedeable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class WildebeestEntity extends Animal implements Stampedeable {

    // 1. Define Data Accessor for syncing the stampede boolean to the client
    private static final EntityDataAccessor<Boolean> STAMPEDING = SynchedEntityData.defineId(WildebeestEntity.class, EntityDataSerializers.BOOLEAN);

    // Stampede settings
    private int stampedeCooldown = 0;
    private static final int STAMPEDE_DURATION = 100; // 5 seconds
    private int stampedeTimer = 0;

    public WildebeestEntity(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
    }

    // 2. Define Attributes (Health, Speed, etc.)
    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 30.0D) // Stronger than a cow
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.5D) // Hard to push
                .add(Attributes.FOLLOW_RANGE, 20.0D);
    }

    // 3. Register AI Goals
    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new StampedeGoal(this));
        this.goalSelector.addGoal(2, new PanicGoal(this, 1.5D));
        this.goalSelector.addGoal(3, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(4, new TemptGoal(this, 1.25D, Ingredient.of(Items.WHEAT), false));
        this.goalSelector.addGoal(5, new FollowParentGoal(this, 1.25D));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
    }

    // 4. Data Synchronization Logic
    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(STAMPEDING, false);
    }

    @Override
    public boolean isStampeding() {
        return this.entityData.get(STAMPEDING);
    }

    @Override
    public void setStampeding(boolean stampeding) {
        this.entityData.set(STAMPEDING, stampeding);
    }

    // 5. Interface Implementation & Logic
    @Override
    public void triggerStampede() {
        if (!this.level().isClientSide && this.stampedeCooldown <= 0) {
            this.setStampeding(true);
            this.stampedeTimer = STAMPEDE_DURATION;
            this.stampedeCooldown = 600; // 30 second cooldown

            // Optional: Alert other wildebeests nearby to stampede too
            this.level().getEntitiesOfClass(WildebeestEntity.class, this.getBoundingBox().inflate(10.0D))
                    .forEach(neighbor -> {
                        if (neighbor != this) neighbor.setStampeding(true);
                    });
        }
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if (!this.level().isClientSide) {
            // Handle Stampede Timer
            if (this.isStampeding()) {
                this.stampedeTimer--;
                if (this.stampedeTimer <= 0) {
                    this.setStampeding(false);
                }
            }

            // Handle Cooldown
            if (this.stampedeCooldown > 0) {
                this.stampedeCooldown--;
            }
        }
    }

    // 6. Breeding & Food
    @Override
    public boolean isFood(ItemStack itemStack) {
        return itemStack.is(Items.WHEAT);
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        return ModEntities.WILDEBEEST.get().create(serverLevel);
    }

    // 7. Sounds (Placeholders - replace with custom sounds if you have them)
    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.COW_AMBIENT;
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.COW_HURT;
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.COW_DEATH;
    }

    @Override
    protected void playStepSound(net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState block) {
        this.playSound(SoundEvents.COW_STEP, 0.15F, 1.0F);
    }

    // 8. Save/Load Data (Persistence)
    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("IsStampeding", this.isStampeding());
        compound.putInt("StampedeCooldown", this.stampedeCooldown);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setStampeding(compound.getBoolean("IsStampeding"));
        this.stampedeCooldown = compound.getInt("StampedeCooldown");
    }
}