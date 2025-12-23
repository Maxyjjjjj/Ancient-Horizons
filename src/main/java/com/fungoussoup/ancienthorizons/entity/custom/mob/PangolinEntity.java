package com.fungoussoup.ancienthorizons.entity.custom.mob;

import com.fungoussoup.ancienthorizons.registry.ModEntities;
import com.fungoussoup.ancienthorizons.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class PangolinEntity extends Animal {

    // Data accessors for syncing data between client and server
    private static final EntityDataAccessor<Boolean> IS_HIDING = SynchedEntityData.defineId(PangolinEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> HIDE_TIME = SynchedEntityData.defineId(PangolinEntity.class, EntityDataSerializers.INT);

    // Animation states
    public AnimationState hideAnimationState = new AnimationState();
    public AnimationState unhideAnimationState = new AnimationState();
    public AnimationState idleAnimationState = new AnimationState();
    public AnimationState walkAnimationState = new AnimationState();

    private int idleAnimationTimeout = 0;

    // Constructor
    public PangolinEntity(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
        this.setCanPickUpLoot(false);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 16.0)
                .add(Attributes.MOVEMENT_SPEED, 0.2)
                .add(Attributes.ARMOR, 4.0)
                .add(Attributes.ARMOR_TOUGHNESS, 2.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.3);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(IS_HIDING, false);
        builder.define(HIDE_TIME, 0);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.4));
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0));
        this.goalSelector.addGoal(3, new TemptGoal(this, 1.2, this::isFood, false));
        this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.1));
        this.goalSelector.addGoal(5, new HideGoal(this));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0f));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));

        // Defensive behavior - hide when near hostile mobs
        this.targetSelector.addGoal(1, new AvoidEntityGoal<>(this, Monster.class, 8.0f, 1.6, 1.4));
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide()) {
            this.setupAnimationStates();
        } else {
            // Handle hiding duration
            if (this.isHiding()) {
                int hideTime = this.getHideTime();
                if (hideTime > 0) {
                    this.setHideTime(hideTime - 1);
                } else {
                    this.setHiding(false);
                }
            }
        }
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);

        // Don't allow interaction while hiding
        if (this.isHiding()) {
            return InteractionResult.PASS;
        }

        // Brush interaction
        if (itemStack.is(Items.BRUSH) && this.canBeBrushed()) {
            return this.brushPangolin(player, itemStack);
        }

        // Feeding interaction
        if (this.isFood(itemStack)) {
            return this.feedFood(player, itemStack);
        }

        return super.mobInteract(player, hand);
    }

    private InteractionResult brushPangolin(Player player, ItemStack brush) {
        if (!this.level().isClientSide()) {
            // Play brushing sound
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                    SoundEvents.ARMADILLO_BRUSH, SoundSource.NEUTRAL, 1.0f, 1.0f);

            // Drop pangolin scute (using armadillo scute as placeholder)
            ItemStack scute = new ItemStack((ItemLike) ModItems.PANGOLIN_SCALE);
            this.spawnAtLocation(scute);

            // Trigger game event
            this.gameEvent(GameEvent.ENTITY_INTERACT, player);

            // Give experience
            if (player instanceof Player) {
                player.giveExperiencePoints(1 + this.random.nextInt(3));
            }

            return InteractionResult.SUCCESS;
        }
        return InteractionResult.CONSUME;
    }

    private InteractionResult feedFood(Player player, ItemStack food) {
        if (!this.level().isClientSide()) {
            // Make pangolin less likely to hide when fed
            if (this.isHiding()) {
                this.setHiding(false);
            }

            // Heal the pangolin
            this.heal(2.0f);

            // Play eating sound
            this.playSound(SoundEvents.GENERIC_EAT, 1.0f, 1.0f);

            // Particle effects
            for (int i = 0; i < 7; i++) {
                Vec3 vec3 = new Vec3(((double)this.random.nextFloat() - 0.5) * 0.1,
                        Math.random() * 0.1 + 0.1,
                        ((double)this.random.nextFloat() - 0.5) * 0.1);
                this.level().addParticle(net.minecraft.core.particles.ParticleTypes.HAPPY_VILLAGER,
                        this.getRandomX(1.0), this.getRandomY() + 0.5, this.getRandomZ(1.0),
                        vec3.x, vec3.y, vec3.z);
            }

            if (!player.getAbilities().instabuild) {
                food.shrink(1);
            }

            return InteractionResult.SUCCESS;
        }
        return InteractionResult.CONSUME;
    }

    @Override
    public boolean isFood(ItemStack itemStack) {
        // Pangolins eat insects and larvae
        return itemStack.is(Items.SPIDER_EYE) ||
                itemStack.is(Items.FERMENTED_SPIDER_EYE) ||
                itemStack.is(Items.HONEYCOMB) ||
                itemStack.is(Items.HONEY_BOTTLE); // Additional food sources
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        return ModEntities.PANGOLIN.get().create(serverLevel);
    }

    // Animation setup
    private void setupAnimationStates() {
        // Walking animation
        if (this.getDeltaMovement().horizontalDistanceSqr() > 1.0E-6 && !this.isHiding()) {
            this.walkAnimationState.startIfStopped(this.tickCount);
        } else {
            this.walkAnimationState.stop();
        }

        // Idle animation
        if (!this.isHiding() && this.getDeltaMovement().horizontalDistanceSqr() <= 1.0E-6) {
            if (this.idleAnimationTimeout <= 0) {
                this.idleAnimationTimeout = 80;
                this.idleAnimationState.start(this.tickCount);
            } else {
                this.idleAnimationTimeout--;
            }
        } else {
            this.idleAnimationState.stop();
        }

        // Hide animation
        if (this.isHiding()) {
            this.hideAnimationState.startIfStopped(this.tickCount);
            this.unhideAnimationState.stop();
        } else {
            this.hideAnimationState.stop();
        }

        // Unhide animation
        if (!this.isHiding() && this.hideAnimationState.isStarted()) {
            this.unhideAnimationState.startIfStopped(this.tickCount);
        }
    }

    // Data accessors
    public boolean isHiding() {
        return this.entityData.get(IS_HIDING);
    }

    public void setHiding(boolean hiding) {
        this.entityData.set(IS_HIDING, hiding);
    }

    public int getHideTime() {
        return this.entityData.get(HIDE_TIME);
    }

    public void setHideTime(int time) {
        this.entityData.set(HIDE_TIME, time);
    }

    public boolean canBeBrushed() {
        return true;
    }

    // NBT data saving/loading
    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("IsHiding", this.isHiding());
        compound.putInt("HideTime", this.getHideTime());
        compound.putBoolean("CanBeBrushed", this.canBeBrushed());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setHiding(compound.getBoolean("IsHiding"));
        this.setHideTime(compound.getInt("HideTime"));
    }

    // Sound methods
    @Override
    protected SoundEvent getAmbientSound() {
        return this.isHiding() ? null : SoundEvents.ARMADILLO_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return this.isHiding() ? SoundEvents.ARMADILLO_HURT_REDUCED : SoundEvents.ARMADILLO_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ARMADILLO_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState blockState) {
        if (!this.isHiding()) {
            this.playSound(SoundEvents.ARMADILLO_STEP, 0.15f, 1.0f);
        }
    }

    // Enhanced hiding behavior
    @Override
    public boolean hurt(DamageSource damageSource, float amount) {
        if (this.isHiding()) {
            // Significantly reduce damage when hiding
            amount *= 0.25f;

            // Play reduced damage sound
            this.playSound(SoundEvents.ARMADILLO_HURT_REDUCED, 1.0f, 1.0f);
        }

        boolean result = super.hurt(damageSource, amount);

        // Trigger hiding when hurt (higher chance if not already hiding)
        if (result && !this.isHiding() && this.random.nextFloat() < 0.9f) {
            this.startHiding();
        }

        return result;
    }

    public void startHiding() {
        if (!this.isHiding()) {
            this.setHiding(true);
            this.setHideTime(100 + this.random.nextInt(200)); // Hide for 5-15 seconds
            this.playSound(SoundEvents.ARMADILLO_ROLL, 1.0f, 1.0f);
            this.getNavigation().stop();
        }
    }

    // Override movement when hiding
    @Override
    public void travel(Vec3 travelVector) {
        if (this.isHiding()) {
            // Pangolin cannot move while hiding
            super.travel(Vec3.ZERO);
        } else {
            super.travel(travelVector);
        }
    }

    // Make pangolin harder to push when hiding
    @Override
    public boolean isPushable() {
        return !this.isHiding() && super.isPushable();
    }

    @Override
    protected void pushEntities() {
        if (!this.isHiding()) {
            super.pushEntities();
        }
    }

    // Custom hiding goal
    private static class HideGoal extends Goal {
        private final PangolinEntity pangolin;
        private int cooldown = 0;

        public HideGoal(PangolinEntity pangolinEntity) {
            this.pangolin = pangolinEntity;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK, Goal.Flag.JUMP));
        }

        @Override
        public boolean canUse() {
            if (this.cooldown > 0) {
                this.cooldown--;
                return false;
            }

            // Hide when threatened, near hostile mobs, or randomly
            return !this.pangolin.isHiding() &&
                    (this.pangolin.getLastHurtByMob() != null ||
                            !this.pangolin.level().getNearbyEntities(Monster.class, TargetingConditions.DEFAULT, this.pangolin, this.pangolin.getBoundingBox().inflate(8.0)).isEmpty() ||
                            this.pangolin.random.nextInt(2000) == 0);
        }

        @Override
        public boolean canContinueToUse() {
            return this.pangolin.isHiding() && this.pangolin.getHideTime() > 0;
        }

        @Override
        public void start() {
            this.pangolin.startHiding();
        }

        @Override
        public void stop() {
            this.cooldown = 400; // 20 second cooldown
        }

        @Override
        public void tick() {
            // Ensure pangolin stays still while hiding
            this.pangolin.getNavigation().stop();
            this.pangolin.setDeltaMovement(this.pangolin.getDeltaMovement().multiply(0.0, 1.0, 0.0));
        }
    }

    // Breeding enhancements
    @Override
    public boolean canMate(Animal otherAnimal) {
        if (this.isHiding() || (otherAnimal instanceof PangolinEntity && ((PangolinEntity) otherAnimal).isHiding())) {
            return false;
        }
        return super.canMate(otherAnimal);
    }
}