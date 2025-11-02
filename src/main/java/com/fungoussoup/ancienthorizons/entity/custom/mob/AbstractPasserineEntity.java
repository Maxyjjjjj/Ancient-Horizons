package com.fungoussoup.ancienthorizons.entity.custom.mob;

import com.fungoussoup.ancienthorizons.entity.ai.BirdNavigation;
import com.fungoussoup.ancienthorizons.entity.ai.ModFollowOwnerGoal;
import com.fungoussoup.ancienthorizons.entity.ai.SemiFlyingFlyGoal;
import com.fungoussoup.ancienthorizons.entity.ai.SemiFlyingMoveControl;
import com.fungoussoup.ancienthorizons.entity.interfaces.DancingAnimal;
import com.fungoussoup.ancienthorizons.entity.interfaces.SemiFlyer;
import com.fungoussoup.ancienthorizons.registry.ModTags;
import com.fungoussoup.ancienthorizons.registry.ModSoundEvents;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.JumpControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.ShoulderRidingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

import java.util.Objects;

public abstract class AbstractPasserineEntity extends ShoulderRidingEntity implements SemiFlyer, DancingAnimal {

    // Entity Data Accessors for syncing data to client
    private static final EntityDataAccessor<Boolean> PASSERINE_FLYING = SynchedEntityData.defineId(AbstractPasserineEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> IS_HOPPING = SynchedEntityData.defineId(AbstractPasserineEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> HOP_TICKS = SynchedEntityData.defineId(AbstractPasserineEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DANCING = SynchedEntityData.defineId(AbstractPasserineEntity.class, EntityDataSerializers.BOOLEAN);

    // Movement state variables
    private int hopTicks;
    private int hopDuration;
    private int hopCooldown;
    private int flapTicks;
    private int chirpCooldown;
    private int restTicks;
    private boolean isResting;
    private boolean isJukeboxing;
    private BlockPos jukeboxPosition;
    public float prevDanceProgress;
    public float danceProgress;

    protected final PathNavigation groundNavigation;
    private final PathNavigation flyingNavigation;

    // Dancing
    public final AnimationState danceAnimationState = new AnimationState();
    private int danceAnimationTimeout = 0;
    private AnimationDefinition currentAnimation = null;

    // Constants
    private static final int MAX_REST_TIME = 300; // 15 seconds
    private static final int MIN_REST_TIME = 100; // 5 seconds
    private static final int CHIRP_COOLDOWN = 100; // 5 seconds
    private static final float FLYING_SPEED_MODIFIER = 1.2F;
    private static final float HOPPING_SPEED_MODIFIER = 0.8F;

    public AbstractPasserineEntity(EntityType<? extends ShoulderRidingEntity> entityType, Level level) {
        super(entityType, level);
        this.groundNavigation = new GroundPathNavigation(this, level);
        this.flyingNavigation = new BirdNavigation(this, level);
    }

    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(PASSERINE_FLYING, false);
        builder.define(IS_HOPPING, false);
        builder.define(HOP_TICKS, 0);
        builder.define(DANCING, false);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.4));
        this.goalSelector.addGoal(1, new BreedGoal(this, 1.0));
        this.goalSelector.addGoal(1, new LandOnOwnersShoulderGoal(this));
        this.goalSelector.addGoal(2, new TemptGoal(this, 1.25f, stack -> stack.is(ModTags.Items.PASSERINE_FOOD), true));
        this.goalSelector.addGoal(2, new ModFollowOwnerGoal(this, 1.25f, 10.0F, 1.0F));
        this.goalSelector.addGoal(2, new FollowParentGoal(this, 1.25f));
        this.goalSelector.addGoal(3, new SemiFlyingFlyGoal(this, 1.0F));
        this.goalSelector.addGoal(4, new PasserineHopGoal(this, 1.0, 40));
        this.goalSelector.addGoal(5, new PasserineRestGoal(this));
        this.goalSelector.addGoal(6, new RandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));

        // Target goals
        this.targetSelector.addGoal(0, new NearestAttackableTargetGoal<>(this, EarthwormEntity.class, false, true));

        // Custom jump control
        this.jumpControl = new PasserineJumpController(this);
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(ModTags.Items.PASSERINE_FOOD);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 6.0)
                .add(Attributes.MOVEMENT_SPEED, 0.25)
                .add(Attributes.FLYING_SPEED, 0.35)
                .add(Attributes.ATTACK_DAMAGE, 1.0);
    }

    @Override
    public boolean canFly() {
        return true;
    }

    @Override
    public boolean shouldGlide() {
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        this.getNavigation().tick();
        if (this.isFlying()) {
            handleFlying();
            // Auto-landing if near ground
            if (this.onGround() || this.verticalCollisionBelow) {
                stopFlying();
            }
        } else {
            if (!this.onGround() && !this.isInWater()) {
                startFlying(); // Recover if pushed off ledge
            }
        }

        prevDanceProgress = danceProgress;
        boolean dance = isDancing();
        if (this.jukeboxPosition == null || !this.jukeboxPosition.closerToCenterThan(this.position(), 15) || !this.level().getBlockState(this.jukeboxPosition).is(Blocks.JUKEBOX)) {
            this.isJukeboxing = false;
            this.setDancing(false);
            this.jukeboxPosition = null;
        }
        if (dance && danceProgress < 5F) {
            danceProgress++;
        }
        if (!dance && danceProgress > 0F) {
            danceProgress--;
        }

        // Handle different movement modes
        if (isFlying()) {
            handleFlying();
        } else if (isHopping()) {
            handleHopping();
        } else {
            handleWalking();
        }

        // Handle rest mechanics
        handleResting();

        // Handle sound effects
        handleSounds();

        // Sync data to client
        syncEntityData();
    }

    private void handleFlying() {
        this.flapTicks++;
        this.resetFallDistance();

        Vec3 motion = this.getDeltaMovement();

        // Hover stabilization
        if (this.random.nextFloat() < 0.02F) {
            double hover = (this.random.nextDouble() - 0.5) * 0.1;
            this.setDeltaMovement(motion.x * 0.9, hover, motion.z * 0.9);
        }

        // If not navigating, circle a bit
        if (this.getNavigation().isDone() && this.random.nextInt(60) == 0) {
            double dx = this.getX() + (this.random.nextDouble() - 0.5) * 8.0;
            double dy = this.getY() + (this.random.nextDouble() - 0.5) * 4.0;
            double dz = this.getZ() + (this.random.nextDouble() - 0.5) * 8.0;
            this.getNavigation().moveTo(dx, dy, dz, 1.0);
        }
    }


    private void handleHopping() {
        if (this.hopTicks < this.hopDuration) {
            this.hopTicks++;
            float hopProgress = (float) this.hopTicks / this.hopDuration;
            float hopHeight = 4.0F * hopProgress * (1.0F - hopProgress);
            // Set Y velocity directly instead of adding
            Vec3 currentVel = this.getDeltaMovement();
            this.setDeltaMovement(currentVel.x, hopHeight * 0.15F, currentVel.z);
        } else {
            setHopping(false);
            this.hopCooldown = 20 + this.random.nextInt(40);
        }
    }

    private void handleWalking() {
        // Handle hop cooldown
        if (this.hopCooldown > 0) {
            this.hopCooldown--;
        }

        // Dampen movement when on ground
        if (this.onGround()) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.9, 0.98, 0.9));

            // Random hop chance
            if (this.hopCooldown <= 0 && this.random.nextFloat() < 0.02F && !isResting) {
                startHopping();
            }
        }
    }

    private void handleResting() {
        if (isResting) {
            this.restTicks++;
            if (this.restTicks > MAX_REST_TIME) {
                setResting(false);
            }
        } else {
            // Random chance to start resting
            if (this.random.nextFloat() < 0.001F && this.onGround() && !isMoving()) {
                setResting(true);
                this.restTicks = 0;
            }
        }
    }

    private void handleSounds() {
        if (this.chirpCooldown > 0) {
            this.chirpCooldown--;
        }

        // Random chirping
        if (this.chirpCooldown <= 0 && this.random.nextFloat() < 0.005F) {
            this.playChirpSound();
            this.chirpCooldown = CHIRP_COOLDOWN + this.random.nextInt(CHIRP_COOLDOWN);
        }
    }

    private void syncEntityData() {
        this.entityData.set(PASSERINE_FLYING, isFlying());
        this.entityData.set(IS_HOPPING, isHopping());
        this.entityData.set(HOP_TICKS, this.hopTicks);
    }

    private boolean isMoving() {
        Vec3 movement = this.getDeltaMovement();
        return movement.horizontalDistanceSqr() > 0.01;
    }

    // Public getters and setters
    public boolean isFlying() {
        return this.entityData.get(PASSERINE_FLYING);
    }

    public void setFlying(boolean flying) {
        if (flying == isFlying()) return;

        this.entityData.set(PASSERINE_FLYING, flying);
        if (flying) {
            this.moveControl = new SemiFlyingMoveControl(this, 10, 9);
            this.navigation = flyingNavigation;
            this.setNoGravity(true);
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.25 * FLYING_SPEED_MODIFIER);
        } else {
            this.moveControl = new MoveControl(this);
            this.navigation = groundNavigation;
            this.setNoGravity(false);
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.25);
        }
    }

    public boolean isHopping() {
        return this.entityData.get(IS_HOPPING);
    }

    public void setHopping(boolean hopping) {
        this.entityData.set(IS_HOPPING, hopping);
        if (hopping) {
            Objects.requireNonNull(this.getAttribute(Attributes.MOVEMENT_SPEED)).setBaseValue(0.25 * HOPPING_SPEED_MODIFIER);
        } else {
            Objects.requireNonNull(this.getAttribute(Attributes.MOVEMENT_SPEED)).setBaseValue(0.25);
        }
    }

    public boolean isResting() {
        return this.isResting;
    }

    public void setResting(boolean resting) {
        this.isResting = resting;
    }

    public void startHopping() {
        if (!isFlying() && !isHopping()) {
            setHopping(true);
            this.hopDuration = 8 + this.random.nextInt(4); // 8-12 ticks
            this.hopTicks = 0;
            this.setJumping(true);
        }
    }

    public void startFlying() {
        if (!isFlying() && !this.isInWaterOrBubble()) {
            setFlying(true);
            this.flapTicks = 0;
            this.setDeltaMovement(this.getDeltaMovement().add(0, 0.4, 0)); // Takeoff boost
        }
    }

    public void stopFlying() {
        if (isFlying()) {
            setFlying(false);
            this.flapTicks = 0;
        }
    }


    public float getHopCompletion(float partialTicks) {
        return this.hopDuration == 0 ? 0.0F :
                Mth.clamp(((float) this.hopTicks + partialTicks) / (float) this.hopDuration, 0.0F, 1.0F);
    }

    public float getFlapCompletion(float partialTicks) {
        return isFlying() ? ((this.flapTicks + partialTicks) % 10) / 10.0F : 0.0F;
    }

    @Override
    protected float getJumpPower() {
        return horizontalCollision ? super.getJumpPower() + 0.1F : 0.2F + random.nextFloat() * 0.1F;
    }

    // Capability checks
    public boolean canWalk() {
        return true;
    }

    public boolean canHop() {
        return true;
    }

    // Sound methods
    protected void playChirpSound() {
        this.playSound(getChirpSound(), 0.6F, 1.0F + (this.random.nextFloat() - 0.5F) * 0.2F);
    }

    protected SoundEvent getChirpSound() {
        return ModSoundEvents.PASSERINE_CHIRP;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return getChirpSound();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.PARROT_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.PARROT_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState blockState) {
        // Birds don't make step sounds
    }

    // Breeding and mating
    @Override
    public boolean canMate(Animal otherAnimal) {
        if (otherAnimal == this) return false;
        if (!(otherAnimal instanceof AbstractPasserineEntity other)) return false;
        return this.isInLove() && other.isInLove() && this.getType() == other.getType();
    }

    // NBT data
    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("IsFlying", isFlying());
        compound.putBoolean("IsHopping", isHopping());
        compound.putBoolean("IsResting", isResting());
        compound.putInt("HopTicks", this.hopTicks);
        compound.putInt("HopDuration", this.hopDuration);
        compound.putInt("HopCooldown", this.hopCooldown);
        compound.putInt("FlapTicks", this.flapTicks);
        compound.putInt("RestTicks", this.restTicks);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        setFlying(compound.getBoolean("IsFlying"));
        setHopping(compound.getBoolean("IsHopping"));
        setResting(compound.getBoolean("IsResting"));
        this.hopTicks = compound.getInt("HopTicks");
        this.hopDuration = compound.getInt("HopDuration");
        this.hopCooldown = compound.getInt("HopCooldown");
        this.flapTicks = compound.getInt("FlapTicks");
        this.restTicks = compound.getInt("RestTicks");
    }

    public int getDanceAnimationTimeout() {
        return danceAnimationTimeout;
    }

    public void setDanceAnimationTimeout(int danceAnimationTimeout) {
        this.danceAnimationTimeout = danceAnimationTimeout;
    }

    public AnimationDefinition getCurrentAnimation() {
        return currentAnimation;
    }

    public void setCurrentAnimation(AnimationDefinition currentAnimation) {
        this.currentAnimation = currentAnimation;
    }

    public boolean isJukeboxing() {
        return isJukeboxing;
    }

    public void setJukeboxing(boolean jukeboxing) {
        isJukeboxing = jukeboxing;
    }

    // Custom AI Goals
    public static class PasserineHopGoal extends Goal {
        private final AbstractPasserineEntity bird;
        private final double speedModifier;
        private final int interval;
        private int nextStartTick;

        public PasserineHopGoal(AbstractPasserineEntity bird, double speedModifier, int interval) {
            this.bird = bird;
            this.speedModifier = speedModifier;
            this.interval = interval;
            this.nextStartTick = reducedTickDelay(interval);
        }

        @Override
        public boolean canUse() {
            if (this.bird.isPassenger() || this.bird.isFlying() || this.bird.isResting() || this.bird.isDancing()) {
                return false;
            }

            if (this.bird.getRandom().nextInt(reducedTickDelay(this.interval)) != 0) {
                return false;
            }

            return this.bird.hopCooldown <= 0 && this.bird.onGround();
        }

        @Override
        public boolean canContinueToUse() {
            return this.bird.isHopping();
        }

        @Override
        public void start() {
            this.bird.startHopping();
        }

        @Override
        public void stop() {
            this.bird.setHopping(false);
        }
    }

    public static class PasserineRestGoal extends Goal {
        private final AbstractPasserineEntity bird;
        private int restTimer;

        public PasserineRestGoal(AbstractPasserineEntity bird) {
            this.bird = bird;
        }

        @Override
        public boolean canUse() {
            if (this.bird.isPassenger() || this.bird.isFlying() || this.bird.isHopping() || this.bird.isDancing()) {
                return false;
            }

            return this.bird.onGround() && this.bird.getRandom().nextFloat() < 0.001F;
        }

        @Override
        public boolean canContinueToUse() {
            return this.bird.isResting() && this.restTimer < MIN_REST_TIME;
        }

        @Override
        public void start() {
            this.bird.setResting(true);
            this.restTimer = 0;
        }

        @Override
        public void stop() {
            this.bird.setResting(false);
            this.restTimer = 0;
        }

        @Override
        public void tick() {
            this.restTimer++;
            this.bird.getNavigation().stop();
        }
    }

    // Custom Jump Controller
    public static class PasserineJumpController extends JumpControl {
        private final AbstractPasserineEntity bird;

        public PasserineJumpController(AbstractPasserineEntity bird) {
            super(bird);
            this.bird = bird;
        }

        @Override
        public void tick() {
            if (this.jump && this.bird.onGround() && !this.bird.isFlying()) {
                if (this.bird.getRandom().nextFloat() < 0.1F) { // Add chance to fly instead
                    this.bird.startFlying();
                } else {
                    this.bird.startHopping();
                }
                this.jump = false;
            }
        }
    }

    public boolean isDancing() {
        return this.entityData.get(DANCING);
    }

    public void setDancing(boolean dancing) {
        this.entityData.set(DANCING, dancing);
        this.isJukeboxing = dancing;
    }

    public void travel(Vec3 vec3d) {
        if (this.isDancing() || danceProgress > 0) {
            if (this.getNavigation().getPath() != null) {
                this.getNavigation().stop();
            }
            vec3d = Vec3.ZERO;
        }
        super.travel(vec3d);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);

        if (itemstack.is(ModTags.Items.PASSERINE_FOOD) && !this.isTame()) {
            if (!player.getAbilities().instabuild) {
                itemstack.shrink(1);
            }

            // 50% chance to tame
            if (this.random.nextInt(2) == 0) {
                this.tame(player);
                this.navigation.stop();
                this.setTarget(null);
                this.setOrderedToSit(true);
                this.level().broadcastEntityEvent(this, (byte) 7);
            } else {
                this.level().broadcastEntityEvent(this, (byte) 6);
            }
            return InteractionResult.SUCCESS;
        }

        if (this.isTame()) {
            if (this.isFood(itemstack) && this.getHealth() < this.getMaxHealth()) {
                FoodProperties foodproperties = itemstack.getFoodProperties(this);
                float f = foodproperties != null ? (float) foodproperties.nutrition() : 1.0F;
                this.heal(f);
                itemstack.consume(1, player);
                this.gameEvent(GameEvent.EAT);
                return InteractionResult.sidedSuccess(this.level().isClientSide());
            }
        }

        if (this.isOwnedBy(player) && !this.isFood(itemstack)) {
            this.setOrderedToSit(!this.isOrderedToSit());
            this.jumping = false;
            this.navigation.stop();
            this.setTarget(null);
            return InteractionResult.SUCCESS;
        }

        return super.mobInteract(player, hand);
    }

    @Override
    public boolean isNoGravity() {
        return this.isFlying() || super.isNoGravity();
    }

    @Override
    public void setJukeboxPos(BlockPos pos) {
        this.jukeboxPosition = pos;
    }
}