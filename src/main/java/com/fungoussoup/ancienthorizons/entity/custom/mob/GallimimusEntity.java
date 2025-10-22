package com.fungoussoup.ancienthorizons.entity.custom.mob;

import com.fungoussoup.ancienthorizons.entity.ModEntities;
import com.fungoussoup.ancienthorizons.registry.ModSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public class GallimimusEntity extends AbstractHorse {

    // Data parameter for syncing stamina to client
    private static final EntityDataAccessor<Integer> DATA_STAMINA =
            SynchedEntityData.defineId(GallimimusEntity.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Boolean> DATA_SPRINTING =
            SynchedEntityData.defineId(GallimimusEntity.class, EntityDataSerializers.BOOLEAN);

    private int maxStamina = 100;
    private int staminaRegenDelay = 0; // delay before stamina starts regenerating
    private static final int REGEN_DELAY_TICKS = 20; // 1-second delay after sprinting

    public GallimimusEntity(EntityType<? extends AbstractHorse> type, Level world) {
        super(type, world);
        this.entityData.set(DATA_STAMINA, this.maxStamina);
        this.entityData.set(DATA_SPRINTING, false);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return AbstractHorse.createBaseHorseAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.5D));
        this.goalSelector.addGoal(1, new RunAroundLikeCrazyGoal(this, 1.2));
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new TemptGoal(this, 1.1D, Ingredient.of(Items.FERN), false));
        this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.1D));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_STAMINA, this.maxStamina);
        builder.define(DATA_SPRINTING, false);
    }

    @Override
    public void tick() {
        super.tick();

        // Only handle stamina logic on server
        if (!this.level().isClientSide) {
            boolean isSprinting = this.entityData.get(DATA_SPRINTING);
            int currentStamina = this.entityData.get(DATA_STAMINA);

            if (isSprinting) {
                // Drain stamina while sprinting
                if (currentStamina > 0) {
                    this.entityData.set(DATA_STAMINA, currentStamina - 1);
                    staminaRegenDelay = REGEN_DELAY_TICKS; // reset regen delay
                } else {
                    // Out of stamina, stop sprinting
                    this.entityData.set(DATA_SPRINTING, false);
                    this.setSprinting(false);
                }
            } else {
                // Handle stamina regeneration
                if (staminaRegenDelay > 0) {
                    staminaRegenDelay--;
                } else if (currentStamina < maxStamina) {
                    // Regenerate stamina (2 points per tick for faster regen)
                    this.entityData.set(DATA_STAMINA, Math.min(currentStamina + 2, maxStamina));
                }
            }
        }
    }

    @Override
    protected void tickRidden(Player player, Vec3 travelVector) {
        super.tickRidden(player, travelVector);

        if (!this.level().isClientSide && this.isVehicle()) {
            boolean wantsToSprint = player.isSprinting();
            int currentStamina = this.entityData.get(DATA_STAMINA);

            if (wantsToSprint && currentStamina > 0) {
                this.entityData.set(DATA_SPRINTING, true);
                this.setSprinting(true);
            } else {
                this.entityData.set(DATA_SPRINTING, false);
                this.setSprinting(false);
            }
        }
    }

    private double getGallimimusNormalSpeed() {
        return this.getAttributeValue(Attributes.MOVEMENT_SPEED);
    }

    private double getGallimimusSprintSpeed() {
        return 0.5D; // A flat value for sprint speed
    }

    // Getter for stamina percentage (used by overlay)
    public float getStaminaPercent() {
        return (float) getStamina() / getMaxStamina();
    }

    public int getStamina() {
        return this.entityData.get(DATA_STAMINA);
    }

    public int getMaxStamina() {
        return maxStamina;
    }

    public boolean isGallimimusSprinting() {
        return this.entityData.get(DATA_SPRINTING);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("Stamina", this.entityData.get(DATA_STAMINA));
        tag.putInt("MaxStamina", this.maxStamina);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("Stamina")) {
            this.entityData.set(DATA_STAMINA, tag.getInt("Stamina"));
        }
        if (tag.contains("MaxStamina")) {
            this.maxStamina = tag.getInt("MaxStamina");
        }
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        return ModEntities.GALLIMIMUS.get().create(serverLevel);
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(Items.FERN);
    }

    // Disable horse-specific behaviors
    @Override
    public boolean canEatGrass() {
        return false;
    }

    @Override
    public int getJumpCooldown() {
        return 0;
    }

    @Override
    public boolean canJump() {
        return false;
    }

    @Override
    public void onPlayerJump(int jumpPower) {
        // Disabled - sprint is handled via the sprint key now
    }

    @Override
    protected float getRiddenSpeed(Player player) {
        // Dynamically return speed based on whether the entity is sprinting
        if (this.isGallimimusSprinting()) {
            return (float) this.getGallimimusSprintSpeed();
        }
        return (float) this.getGallimimusNormalSpeed();
    }

    @Override
    protected @Nullable SoundEvent getAmbientSound() {
        return ModSoundEvents.GALLIMIMUS_AMBIENT;
    }

    @Override
    protected @Nullable SoundEvent getHurtSound(DamageSource damageSource) {
        return ModSoundEvents.GALLIMIMUS_HURT;
    }

    @Override
    protected @Nullable SoundEvent getDeathSound() {
        return ModSoundEvents.GALLIMIMUS_DEATH;
    }

    @Override
    protected @Nullable SoundEvent getAngrySound() {
        return ModSoundEvents.GALLIMIMUS_ANGRY;
    }

    @Override
    public void makeMad() {
        super.makeMad();
    }

    @Override
    protected boolean canPerformRearing() {
        return false;
    }

    @Override
    protected @Nullable SoundEvent getEatingSound() {
        return SoundEvents.PARROT_EAT;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState block) {
        SoundType soundtype = block.getSoundType(this.level(), pos, this);
        this.playSound(SoundEvents.PANDA_STEP, soundtype.getVolume() * 0.15F, soundtype.getPitch());
    }

    @Override
    protected void playGallopSound(SoundType soundType) {
        this.playSound(SoundEvents.PANDA_STEP, soundType.getVolume() * 0.15F, soundType.getPitch());
    }
}