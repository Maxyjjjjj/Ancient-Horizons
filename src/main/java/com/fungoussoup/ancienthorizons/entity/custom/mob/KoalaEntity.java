package com.fungoussoup.ancienthorizons.entity.custom.mob;

import com.fungoussoup.ancienthorizons.entity.ai.ArborealPathNavigation;
import com.fungoussoup.ancienthorizons.entity.interfaces.ArborealAnimal;
import com.fungoussoup.ancienthorizons.registry.ModEntities;
import com.fungoussoup.ancienthorizons.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

/**
 * Koala Entity - Adorably stupid marsupials that:
 * - Only breed near eucalyptus trees
 * - Are too dumb to recognize food when hand-fed
 * - Sleep most of the time
 * - Climb very slowly
 */
public class KoalaEntity extends Animal implements ArborealAnimal {

    private static final EntityDataAccessor<Boolean> DATA_SLEEPING =
            SynchedEntityData.defineId(KoalaEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_CLINGING =
            SynchedEntityData.defineId(KoalaEntity.class, EntityDataSerializers.BOOLEAN);

    private static final int EUCALYPTUS_SEARCH_RANGE = 8;
    private int sleepTimer = 0;

    public final AnimationState climbAnimationState = new AnimationState();
    public final AnimationState sitAnimationState = new AnimationState();

    public KoalaEntity(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_SLEEPING, false);
        builder.define(DATA_CLINGING, false);
    }

    // --- Attributes ---
    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 14.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.15D) // Very slow
                .add(Attributes.FOLLOW_RANGE, 6.0D); // Don't care about much
    }

    // --- Goals ---
    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.0D)); // Even panic is slow
        this.goalSelector.addGoal(2, new KoalaSleepGoal(this));
        this.goalSelector.addGoal(3, new KoalaBreedGoal(this, 0.8D));
        this.goalSelector.addGoal(4, new FollowParentGoal(this, 0.9D));
        this.goalSelector.addGoal(5, new FindEucalyptusGoal(this, 0.9D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(8, new WaterAvoidingRandomStrollGoal(this, 0.8D));
    }

    @Override
    public boolean isFood(ItemStack stack) {
        // Koalas only "recognize" eucalyptus leaves as food
        return stack.is(ModBlocks.EUCALYPTUS_LEAVES.asItem());
    }

    // --- Breeding ---
    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob mate) {
        return ModEntities.KOALA.get().create(level);
    }

    @Override
    public boolean canMate(Animal other) {
        if (!super.canMate(other)) {
            return false;
        }

        // Must have eucalyptus nearby to breed
        return hasEucalyptusNearby();
    }

    /**
     * Checks if there's a eucalyptus tree within range.
     */
    private boolean hasEucalyptusNearby() {
        BlockPos pos = this.blockPosition();
        for (int x = -EUCALYPTUS_SEARCH_RANGE; x <= EUCALYPTUS_SEARCH_RANGE; x++) {
            for (int y = -EUCALYPTUS_SEARCH_RANGE; y <= EUCALYPTUS_SEARCH_RANGE; y++) {
                for (int z = -EUCALYPTUS_SEARCH_RANGE; z <= EUCALYPTUS_SEARCH_RANGE; z++) {
                    BlockPos checkPos = pos.offset(x, y, z);
                    Block block = level().getBlockState(checkPos).getBlock();
                    if (block == ModBlocks.EUCALYPTUS_LOG.get() ||
                            block == ModBlocks.EUCALYPTUS_LEAVES.get()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // --- Player Interaction ---
    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        // Koalas are too dumb to recognize hand-fed food
        // They just stare blankly at you
        if (this.isFood(stack)) {
            if (!this.level().isClientSide) {
                if (!this.hasEucalyptusNearby()) {
                    this.playSound(SoundEvents.PANDA_WORRIED_AMBIENT, 0.6F, 1.2F);

                    // Just look at the player, doing nothing
                    this.getLookControl().setLookAt(player, 30.0F, 30.0F);
                }
            }
            return InteractionResult.SUCCESS;
        }

        return super.mobInteract(player, hand);
    }

    // --- Behavior Ticks ---
    @Override
    public void tick() {
        super.tick();

        if (!level().isClientSide) {
            if (sleepTimer > 0) sleepTimer--;
        }

        // Handle climbing (very slowly)
        if (!isSleeping()) {
            handleClimbing(this);
        }

        if (this.level().isClientSide()) {
            // Manage Animation States on the client
            this.setupAnimationStates();
        } else {
            // Logic on the server: Are we touching a climbable block?
            boolean isTouchingTree = this.horizontalCollision;
            this.setClinging(isTouchingTree && !this.onGround());
        }
    }

    private void setupAnimationStates() {
        if (this.isSleeping()) {
            this.sitAnimationState.startIfStopped(this.tickCount);
            this.climbAnimationState.stop();
        } else if (this.isClinging()) {
            this.climbAnimationState.startIfStopped(this.tickCount);
            this.sitAnimationState.stop();
        } else {
            this.sitAnimationState.stop();
            this.climbAnimationState.stop();
        }
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        return new ArborealPathNavigation(this, level);
    }

    @Override
    public void travel(Vec3 travelVector) {
        if (this.isEffectiveAi()) {
            // Apply vertical movement logic
            this.move(MoverType.SELF, this.getDeltaMovement());

            // Custom vertical friction/speed
            double speed = this.getClimbingSpeed();
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.5, 1.0, 0.5));

            if (this.jumping) {
                this.setDeltaMovement(this.getDeltaMovement().add(0, speed, 0));
            } else if (this.isShiftKeyDown()) {
                this.setDeltaMovement(this.getDeltaMovement().add(0, -speed, 0));
            } else {
                // Stay stuck to the tree (antigravity)
                this.setDeltaMovement(this.getDeltaMovement().x, 0.0, this.getDeltaMovement().z);
            }
        } else {
            super.travel(travelVector);
        }
    }

    // --- ArborealAnimal Implementation ---
    @Override
    public double getClimbingSpeed() {
        return 0.08; // Very slow climbers
    }

    @Override
    public boolean canClimb() {
        return !this.isSleeping() && !this.isInPowderSnow;
    }

    @Override
    public boolean isClimbableBlock(net.minecraft.world.level.block.state.BlockState state) {
        // Only climb eucalyptus trees
        Block block = state.getBlock();
        return block == ModBlocks.EUCALYPTUS_LOG.get() ||
                block == ModBlocks.EUCALYPTUS_LEAVES.get();
    }

    // --- Sounds ---
    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return isSleeping() ? null : SoundEvents.PANDA_AMBIENT;
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.PANDA_HURT;
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.PANDA_DEATH;
    }

    @Override
    protected float getSoundVolume() {
        return 0.5F;
    }

    @Override
    public int getAmbientSoundInterval() {
        return 200; // Very quiet animals
    }

    public boolean isSleeping() {
        return this.entityData.get(DATA_SLEEPING);
    }

    public void setSleeping(boolean sleeping) {
        this.entityData.set(DATA_SLEEPING, sleeping);
    }

    public boolean isClinging() {
        return this.entityData.get(DATA_CLINGING);
    }

    public void setClinging(boolean clinging) {
        this.entityData.set(DATA_CLINGING, clinging);
    }

    // --- Custom Goals ---

    /**
     * Koalas sleep 18-22 hours a day. This goal makes them sleep frequently.
     */
    private static class KoalaSleepGoal extends Goal {
        private final KoalaEntity koala;
        private int sleepDuration = 0;

        KoalaSleepGoal(KoalaEntity koala) {
            this.koala = koala;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP));
        }

        @Override
        public boolean canUse() {
            // 70% chance to want to sleep when not already sleeping
            return koala.onGround() &&
                    !koala.isSleeping() &&
                    koala.random.nextInt(100) < 70;
        }

        @Override
        public void start() {
            // Sleep for 10-30 seconds (200-600 ticks)
            sleepDuration = 200 + koala.random.nextInt(400);
            koala.getNavigation().stop();
            koala.setSleeping(true);
        }

        @Override
        public void stop() {
            koala.setSleeping(false);
            sleepDuration = 0;
        }

        @Override
        public boolean canContinueToUse() {
            return sleepDuration-- > 0 && !koala.isInWater();
        }

        @Override
        public void tick() {
            koala.getNavigation().stop();
        }
    }

    /**
     * Special breed goal that checks for eucalyptus nearby
     */
    private static class KoalaBreedGoal extends BreedGoal {
        private final KoalaEntity koala;

        KoalaBreedGoal(KoalaEntity koala, double speedModifier) {
            super(koala, speedModifier);
            this.koala = koala;
        }

        @Override
        public boolean canUse() {
            // Only attempt breeding if eucalyptus is nearby
            return super.canUse() && koala.hasEucalyptusNearby();
        }
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        if (DATA_SLEEPING.equals(key)) {
            if (this.isSleeping()) {
                this.sitAnimationState.start(this.tickCount);
            } else {
                this.sitAnimationState.stop();
            }
        }
        // You can add logic for DATA_CLINGING -> climbAnimationState here similarly
        super.onSyncedDataUpdated(key);
    }

    /**
     * Goal to wander towards eucalyptus trees
     */
    private static class FindEucalyptusGoal extends Goal {
        private final KoalaEntity koala;
        private final double speedModifier;
        private BlockPos targetPos;

        FindEucalyptusGoal(KoalaEntity koala, double speedModifier) {
            this.koala = koala;
            this.speedModifier = speedModifier;
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if (koala.isSleeping() || koala.random.nextInt(100) != 0) {
                return false;
            }

            // Look for eucalyptus
            BlockPos pos = koala.blockPosition();
            for (int x = -8; x <= 8; x++) {
                for (int y = -4; y <= 4; y++) {
                    for (int z = -8; z <= 8; z++) {
                        BlockPos checkPos = pos.offset(x, y, z);
                        Block block = koala.level().getBlockState(checkPos).getBlock();
                        if (block == ModBlocks.EUCALYPTUS_LOG.get() ||
                                block == ModBlocks.EUCALYPTUS_LEAVES.get()) {
                            targetPos = checkPos;
                            return true;
                        }
                    }
                }
            }
            return false;
        }

        @Override
        public void start() {
            if (targetPos != null) {
                koala.getNavigation().moveTo(
                        targetPos.getX(),
                        targetPos.getY(),
                        targetPos.getZ(),
                        speedModifier
                );
            }
        }

        @Override
        public void tick() {
            if (targetPos != null) {
                // If we are close to the tree, try to move "up"
                if (koala.distanceToSqr(targetPos.getCenter()) < 2.0D) {
                    koala.setJumping(true); // Encourages the AI to "step up" onto the log
                }
                koala.getNavigation().moveTo(targetPos.getX(), targetPos.getY(), targetPos.getZ(), speedModifier);
            }
        }

        @Override
        public boolean canContinueToUse() {
            return !koala.getNavigation().isDone() && !koala.isSleeping();
        }

        @Override
        public void stop() {
            targetPos = null;
            koala.getNavigation().stop();
        }
    }
}