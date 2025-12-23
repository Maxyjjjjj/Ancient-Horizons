package com.fungoussoup.ancienthorizons.entity.custom.mob;

import com.fungoussoup.ancienthorizons.registry.ModEntities;
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

    private static final EntityDataAccessor<Integer> DATA_STAMINA =
            SynchedEntityData.defineId(GallimimusEntity.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Boolean> DATA_SPRINTING =
            SynchedEntityData.defineId(GallimimusEntity.class, EntityDataSerializers.BOOLEAN);

    private static final EntityDataAccessor<Boolean> DATA_RECHARGING =
            SynchedEntityData.defineId(GallimimusEntity.class, EntityDataSerializers.BOOLEAN);

    private int maxStamina = 200; // Increased for longer sprints
    private int staminaRegenDelay = 0;
    private static final int REGEN_DELAY_TICKS = 40; // 2-second delay after sprinting stops
    private static final int STAMINA_DRAIN_RATE = 1; // Stamina per tick while sprinting
    private static final int STAMINA_REGEN_RATE = 3; // Stamina per tick while recharging

    public GallimimusEntity(EntityType<? extends AbstractHorse> type, Level world) {
        super(type, world);
        this.entityData.set(DATA_STAMINA, this.maxStamina);
        this.entityData.set(DATA_SPRINTING, false);
        this.entityData.set(DATA_RECHARGING, false);
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
        builder.define(DATA_RECHARGING, false);
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide) {
            boolean isSprinting = this.entityData.get(DATA_SPRINTING);
            int currentStamina = this.entityData.get(DATA_STAMINA);

            if (isSprinting) {
                // Drain stamina while sprinting
                if (currentStamina > 0) {
                    this.entityData.set(DATA_STAMINA, Math.max(0, currentStamina - STAMINA_DRAIN_RATE));
                    this.entityData.set(DATA_RECHARGING, false);
                    staminaRegenDelay = REGEN_DELAY_TICKS;
                } else {
                    // Out of stamina, force stop sprinting
                    this.stopSprinting();
                }
            } else {
                // Handle stamina regeneration
                if (staminaRegenDelay > 0) {
                    staminaRegenDelay--;
                    this.entityData.set(DATA_RECHARGING, false);
                } else if (currentStamina < maxStamina) {
                    // Actively recharging
                    this.entityData.set(DATA_RECHARGING, true);
                    this.entityData.set(DATA_STAMINA, Math.min(currentStamina + STAMINA_REGEN_RATE, maxStamina));
                } else {
                    // Fully charged
                    this.entityData.set(DATA_RECHARGING, false);
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
                // Start or continue sprinting
                if (!this.isGallimimusSprinting()) {
                    this.startSprinting();
                }
            } else {
                // Stop sprinting if not holding jump or out of stamina
                if (this.isGallimimusSprinting()) {
                    this.stopSprinting();
                }
            }
        }
    }

    private void startSprinting() {
        this.entityData.set(DATA_SPRINTING, true);
        this.setSprinting(true);
    }

    private void stopSprinting() {
        this.entityData.set(DATA_SPRINTING, false);
        this.setSprinting(false);
    }

    private double getGallimimusNormalSpeed() {
        return this.getAttributeValue(Attributes.MOVEMENT_SPEED);
    }

    private double getGallimimusSprintSpeed() {
        return 0.6D; // Increased sprint speed for more dramatic effect
    }

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

    public boolean isRecharging() {
        return this.entityData.get(DATA_RECHARGING);
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
        return false; // Disable jumping, jump key used for sprint
    }

    @Override
    public void onPlayerJump(int jumpPower) {
        // Disabled - jump key now controls sprinting
    }

    @Override
    protected float getRiddenSpeed(Player player) {
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
        if (this.isGallimimusSprinting()) {
            this.playSound(SoundEvents.PANDA_STEP, soundtype.getVolume() * 0.3F, soundtype.getPitch() * 1.2F);
        } else {
            this.playSound(SoundEvents.PANDA_STEP, soundtype.getVolume() * 0.15F, soundtype.getPitch());
        }
    }

    @Override
    protected void playGallopSound(SoundType soundType) {
        if (this.isGallimimusSprinting()) {
            this.playSound(SoundEvents.PANDA_STEP, soundType.getVolume() * 0.3F, soundType.getPitch() * 1.2F);
        } else {
            this.playSound(SoundEvents.PANDA_STEP, soundType.getVolume() * 0.15F, soundType.getPitch());
        }
    }
}