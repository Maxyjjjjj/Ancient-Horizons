package com.fungoussoup.ancienthorizons.entity.custom.mob;

import com.fungoussoup.ancienthorizons.registry.ModEntities;
import com.fungoussoup.ancienthorizons.entity.interfaces.CuriousAndIntelligentAnimal;
import com.fungoussoup.ancienthorizons.entity.interfaces.DancingAnimal;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.*;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.List;
import java.util.UUID;

public class LatenivenatrixEntity extends TamableAnimal implements DancingAnimal, NeutralMob, CuriousAndIntelligentAnimal {

    // Data Sync for animations and states
    private static final EntityDataAccessor<Boolean> LATEN_DANCING = SynchedEntityData.defineId(LatenivenatrixEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> LATEN_STALKING = SynchedEntityData.defineId(LatenivenatrixEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> COLLAR_COLOR_ID = SynchedEntityData.defineId(LatenivenatrixEntity.class, EntityDataSerializers.INT);

    // Logic variables
    private @Nullable BlockPos jukeboxPosition;
    private int stealthTimer = 0;
    private int distractionCooldown = 0;

    // Taming ingredients
    private static final Ingredient TAME_FOOD = Ingredient.of(Items.RABBIT, Items.COOKED_RABBIT, Items.CHICKEN);

    public LatenivenatrixEntity(EntityType<? extends TamableAnimal> entityType, Level level) {
        super(entityType, level);
        ((GroundPathNavigation)this.getNavigation()).setCanOpenDoors(true);
        this.setCanPickUpLoot(true);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return TamableAnimal.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 24.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.35D)
                .add(Attributes.ATTACK_DAMAGE, 6.0D)
                .add(Attributes.FOLLOW_RANGE, 32.0D)
                .add(Attributes.ARMOR, 2.0D); // Slight defense boost
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(2, new OpenDoorGoal(this, true));

        // WARDEN-SPECIFIC TACTICS (High Priority when fighting Wardens)
        this.goalSelector.addGoal(3, new WardenTacticsGoal(this));

        // Standard Combat
        this.goalSelector.addGoal(4, new LeapAtTargetGoal(this, 0.4F));
        this.goalSelector.addGoal(5, new MeleeAttackGoal(this, 1.2D, true));

        // Social & Taming
        this.goalSelector.addGoal(6, new FollowOwnerGoal(this, 1.0D, 10.0F, 2.0F));
        this.goalSelector.addGoal(7, new BreedGoal(this, 1.0D));

        // Idle behaviors
        this.goalSelector.addGoal(8, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(10, new RandomLookAroundGoal(this));

        // TARGET SELECTORS
        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(3, new PackCoordinationGoal(this));
        this.targetSelector.addGoal(4, (new HurtByTargetGoal(this)).setAlertOthers());

        // Prey targets
        this.targetSelector.addGoal(6, new NonTameRandomTargetGoal<>(this, Zombie.class, false, null));
        this.targetSelector.addGoal(7, new NonTameRandomTargetGoal<>(this, Chicken.class, false, null));
        this.targetSelector.addGoal(7, new NonTameRandomTargetGoal<>(this, Sheep.class, false, null));
        this.targetSelector.addGoal(7, new NonTameRandomTargetGoal<>(this, Cow.class, false, null));
        this.targetSelector.addGoal(7, new NonTameRandomTargetGoal<>(this, Pig.class, false, null));
        this.targetSelector.addGoal(7, new NonTameRandomTargetGoal<>(this, DomesticGoatEntity.class, false, null));
        this.targetSelector.addGoal(7, new NonTameRandomTargetGoal<>(this, PheasantEntity.class, false, null));

        // Warden hunting - dangerous but thrilling
        this.targetSelector.addGoal(8, new NonTameRandomTargetGoal<>(this, Warden.class, false, null));

        // Neutral Mob
        this.targetSelector.addGoal(4, new NonTameRandomTargetGoal<>(this, LivingEntity.class, true, this::isAngryAt));
        this.targetSelector.addGoal(9, new ResetUniversalAngerTargetGoal<>(this, false));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(LATEN_DANCING, false);
        builder.define(LATEN_STALKING, false);
        builder.define(COLLAR_COLOR_ID, DyeColor.RED.getId());
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("Dancing", this.isDancing());
        compound.putBoolean("Stalking", this.isStalking());
        compound.putInt("DistractionCooldown", this.distractionCooldown);
        compound.putBoolean("Sitting", isOrderedToSit());
        compound.putInt("CollarColor", getCollarColor().getId());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setDancing(compound.getBoolean("Dancing"));
        this.setStalking(compound.getBoolean("Stalking"));
        this.distractionCooldown = compound.getInt("DistractionCooldown");
        compound.putBoolean("Sitting", isOrderedToSit());
        this.setCollarColor(DyeColor.byId(compound.getInt("CollarColor")));
    }

    public boolean isDancing() {
        return this.entityData.get(LATEN_DANCING);
    }

    @Override
    public void setDancing(boolean dancing) {
        this.entityData.set(LATEN_DANCING, dancing);
    }

    public boolean isStalking() {
        return this.entityData.get(LATEN_STALKING);
    }

    public void setStalking(boolean stalking) {
        this.entityData.set(LATEN_STALKING, stalking);
    }

    @Override
    public void setJukeboxPos(BlockPos pos) {
        this.jukeboxPosition = pos;
    }

    @Override
    public void aiStep() {
        super.aiStep();

        // Dancing Logic
        if (this.jukeboxPosition != null) {
            if (this.level().isClientSide) return;

            if (this.jukeboxPosition.distSqr(this.blockPosition()) > 12.0D ||
                    !this.level().getBlockState(this.jukeboxPosition).is(net.minecraft.world.level.block.Blocks.JUKEBOX)) {
                this.setDancing(false);
                this.jukeboxPosition = null;
            } else {
                this.setDancing(true);
            }
        }

        // Stealth/Stalking management
        if (this.stealthTimer > 0) {
            this.stealthTimer--;
            if (this.stealthTimer == 0) {
                this.setStalking(false);
                this.setPose(Pose.STANDING);
            }
        }

        // Distraction cooldown
        if (this.distractionCooldown > 0) {
            this.distractionCooldown--;
        }
    }

    public void enterStealthMode(int duration) {
        this.stealthTimer = duration;
        this.setStalking(true);
        this.setPose(Pose.CROUCHING);
        this.setSilent(true); // Reduce detection
    }

    public void exitStealthMode() {
        this.stealthTimer = 0;
        this.setStalking(false);
        this.setPose(Pose.STANDING);
        this.setSilent(false);
    }

    public boolean canCreateDistraction() {
        return this.distractionCooldown <= 0;
    }

    public void createDistraction(Vec3 position) {
        if (!this.level().isClientSide && canCreateDistraction()) {
            // Create a sound/vibration at target location to confuse Warden
            this.level().playSound(null, BlockPos.containing(position),
                    SoundEvents.STONE_BREAK, SoundSource.BLOCKS, 1.0F, 1.0F);
            this.level().gameEvent(this, GameEvent.BLOCK_PLACE, BlockPos.containing(position));
            this.distractionCooldown = 100; // 5 second cooldown
        }
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);

        if (!this.isTame() && TAME_FOOD.test(itemStack)) {
            if (!player.getAbilities().instabuild) {
                itemStack.shrink(1);
            }
            if (!this.level().isClientSide) {
                if (this.random.nextInt(3) == 0) {
                    this.tame(player);
                    this.navigation.stop();
                    this.setTarget(null);
                    this.level().broadcastEntityEvent(this, (byte)7);
                } else {
                    this.level().broadcastEntityEvent(this, (byte)6);
                }
            }
            return InteractionResult.SUCCESS;
        }

        if (this.isTame() && !this.isFood(itemStack) && player.isSecondaryUseActive()) {
            if (!this.level().isClientSide) {
                this.setOrderedToSit(!this.isOrderedToSit());
            }
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }

        return super.mobInteract(player, hand);
    }

    @Override
    public boolean isFood(ItemStack itemStack) {
        return itemStack.is(Items.RABBIT) || itemStack.is(Items.COOKED_RABBIT) ||
                itemStack.is(Items.CHICKEN) || itemStack.is(Items.ROTTEN_FLESH);
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        LatenivenatrixEntity baby = ModEntities.LATENIVENATRIX.get().create(serverLevel);
        if (baby != null && this.isTame()) {
            baby.setTame(true, true);
            baby.setOwnerUUID(this.getOwnerUUID());
        }
        return baby;
    }

    protected void applyTamingSideEffects() {
        if (this.isTame()) {
            this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(48.0F);
            this.setHealth(48.0F);
        } else {
            this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(24.0F);
        }
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.PARROT_IMITATE_WARDEN;
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.GENERIC_HURT;
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.GENERIC_DEATH;
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (key.equals(COLLAR_COLOR_ID)) {
            this.getCollarColor();
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        // Enhanced dodge mechanics - 25% for projectiles, 40% for sonic boom (they've learned!)
        if (source.is(DamageTypes.SONIC_BOOM)) {
            if (this.random.nextFloat() < 0.40F) {
                this.level().broadcastEntityEvent(this, (byte) 6);
                // Quick leap away from danger
                Vec3 leapVec = this.getDeltaMovement();
                this.setDeltaMovement(leapVec.x * 1.5, 0.5, leapVec.z * 1.5);
                return false;
            }
        } else if (source.is(DamageTypes.ARROW)) {
            if (this.random.nextFloat() < 0.25F) {
                this.level().broadcastEntityEvent(this, (byte) 6);
                return false;
            }
        }

        // Exit stealth if hit
        if (this.isStalking()) {
            this.exitStealthMode();
        }

        return super.hurt(source, amount);
    }

    // NeutralMob implementation (required for some behaviors)
    @Override
    public int getRemainingPersistentAngerTime() {
        return 0;
    }

    @Override
    public void setRemainingPersistentAngerTime(int i) {}

    @Override
    public @Nullable UUID getPersistentAngerTarget() {
        return null;
    }

    @Override
    public void setPersistentAngerTarget(@Nullable UUID uuid) {}

    @Override
    public void startPersistentAngerTimer() {}

    // Collars!

    public DyeColor getCollarColor() {
        int id = this.entityData.get(COLLAR_COLOR_ID);
        return DyeColor.byId(Mth.clamp(id, 0, DyeColor.values().length - 1));
    }

    public void setCollarColor(DyeColor color) {
        if (color != null) {
            this.entityData.set(COLLAR_COLOR_ID, color.getId());
        }
    }

    // ==================== ADVANCED WARDEN HUNTING AI ====================

    /**
     * Pack Coordination Goal - Shares targeting info with nearby pack members
     */
    static class PackCoordinationGoal extends Goal {
        private final LatenivenatrixEntity mob;
        private static final double PACK_RANGE = 16.0D;

        public PackCoordinationGoal(LatenivenatrixEntity mob) {
            this.mob = mob;
            this.setFlags(EnumSet.of(Flag.TARGET));
        }

        @Override
        public boolean canUse() {
            return this.mob.getTarget() != null;
        }

        @Override
        public void tick() {
            LivingEntity target = this.mob.getTarget();
            if (target == null) return;

            // Alert nearby pack members
            List<LatenivenatrixEntity> packMembers = this.mob.level()
                    .getEntitiesOfClass(LatenivenatrixEntity.class,
                            this.mob.getBoundingBox().inflate(PACK_RANGE));

            for (LatenivenatrixEntity packMate : packMembers) {
                if (packMate != this.mob && packMate.getTarget() == null) {
                    packMate.setTarget(target);
                }
            }
        }
    }

    /**
     * Enhanced Warden Tactics - Hit-and-run, stealth, distractions, and pack coordination
     */
    static class WardenTacticsGoal extends Goal {
        private final LatenivenatrixEntity mob;
        private LivingEntity target;
        private int retreatTimer = 0;
        private int attackCooldown = 0;
        private int tacticalTimer = 0;
        private TacticPhase currentPhase = TacticPhase.APPROACH;

        private enum TacticPhase {
            APPROACH,    // Sneak close
            STRIKE,      // Quick attack
            RETREAT,     // Get away
            CIRCLE,      // Observe and wait
            DISTRACT     // Create decoy sounds
        }

        public WardenTacticsGoal(LatenivenatrixEntity mob) {
            this.mob = mob;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = this.mob.getTarget();
            if (target != null && target.isAlive()) {
                this.target = target;
                return true;
            }
            return false;
        }

        @Override
        public void stop() {
            this.target = null;
            this.mob.exitStealthMode();
            this.currentPhase = TacticPhase.APPROACH;
        }

        @Override
        public void tick() {
            if (this.target == null || !this.target.isAlive()) {
                return;
            }

            double distanceSq = this.mob.distanceToSqr(this.target);
            boolean isWarden = this.target instanceof Warden;
            this.tacticalTimer++;

            // PHASE SYSTEM for intelligent combat
            switch (this.currentPhase) {
                case APPROACH:
                    handleApproach(distanceSq, isWarden);
                    break;
                case STRIKE:
                    handleStrike(distanceSq);
                    break;
                case RETREAT:
                    handleRetreat(distanceSq, isWarden);
                    break;
                case CIRCLE:
                    handleCircle(distanceSq, isWarden);
                    break;
                case DISTRACT:
                    handleDistraction(distanceSq, isWarden);
                    break;
            }

            // Decrement timers
            if (this.retreatTimer > 0) this.retreatTimer--;
            if (this.attackCooldown > 0) this.attackCooldown--;
        }

        private void handleApproach(double distanceSq, boolean isWarden) {
            if (isWarden && distanceSq > 64.0D) {
                // STEALTH APPROACH: Crouch when far from Warden
                this.mob.enterStealthMode(200);
                this.mob.setSprinting(false); // No vibrations
                this.mob.getNavigation().moveTo(this.target, 0.8D);
            } else {
                this.mob.exitStealthMode();
                this.mob.getNavigation().moveTo(this.target, 1.2D);
            }

            // Transition to STRIKE when close enough
            if (distanceSq < 9.0D && this.attackCooldown <= 0) {
                this.currentPhase = TacticPhase.STRIKE;
            }
        }

        private void handleStrike(double distanceSq) {
            if (distanceSq < 9.0D) {
                // Quick strike
                this.mob.exitStealthMode();
                this.mob.doHurtTarget(this.target);
                this.attackCooldown = 30; // 1.5 second attack cooldown

                // Immediately retreat after attacking
                this.currentPhase = TacticPhase.RETREAT;
                this.retreatTimer = 60; // Retreat for 3 seconds
            }
        }

        private void handleRetreat(double distanceSq, boolean isWarden) {
            // Sprint away to safe distance
            Vec3 avoidPos = DefaultRandomPos.getPosAway(this.mob, 12, 7, this.target.position());
            if (avoidPos != null) {
                this.mob.getNavigation().moveTo(avoidPos.x, avoidPos.y, avoidPos.z, 1.6D);
            }

            if (this.retreatTimer <= 0) {
                // After retreating, decide next phase
                if (isWarden && this.mob.canCreateDistraction() && this.mob.random.nextFloat() < 0.4F) {
                    this.currentPhase = TacticPhase.DISTRACT;
                } else {
                    this.currentPhase = TacticPhase.CIRCLE;
                }
            }
        }

        private void handleCircle(double distanceSq, boolean isWarden) {
            this.mob.getLookControl().setLookAt(this.target, 30.0F, 30.0F);

            // Circle around target at medium distance (8-12 blocks)
            if (distanceSq < 64.0D) {
                // Too close, back up
                Vec3 avoidPos = DefaultRandomPos.getPosAway(this.mob, 10, 5, this.target.position());
                if (avoidPos != null) {
                    this.mob.getNavigation().moveTo(avoidPos.x, avoidPos.y, avoidPos.z, 1.1D);
                }
            } else if (distanceSq > 196.0D) {
                // Too far, approach
                this.currentPhase = TacticPhase.APPROACH;
            } else {
                // Perfect distance - strafe around target
                Vec3 strafePos = getStrafePosition();
                this.mob.getNavigation().moveTo(strafePos.x, strafePos.y, strafePos.z, 1.0D);
            }

            // Crouch if Warden and far enough
            if (isWarden && distanceSq > 100.0D) {
                this.mob.enterStealthMode(40);
            }

            // Wait for cooldowns, then approach again
            if (this.attackCooldown <= 0 && this.tacticalTimer > 80) {
                this.currentPhase = TacticPhase.APPROACH;
                this.tacticalTimer = 0;
            }
        }

        private void handleDistraction(double distanceSq, boolean isWarden) {
            if (!isWarden) {
                this.currentPhase = TacticPhase.CIRCLE;
                return;
            }

            // Create distraction sound away from mob's position
            Vec3 distractionPos = this.target.position().add(
                    this.mob.random.nextDouble() * 10 - 5,
                    0,
                    this.mob.random.nextDouble() * 10 - 5
            );

            this.mob.createDistraction(distractionPos);

            // After creating distraction, circle or approach from different angle
            this.currentPhase = TacticPhase.CIRCLE;
            this.tacticalTimer = 0;
        }

        private Vec3 getStrafePosition() {
            // Calculate a position perpendicular to target direction for strafing
            double angle = Math.atan2(
                    this.target.getZ() - this.mob.getZ(),
                    this.target.getX() - this.mob.getX()
            );

            // Add 90 degrees for perpendicular movement
            angle += Math.PI / 2 * (this.mob.random.nextBoolean() ? 1 : -1);

            double distance = 10.0D;
            double x = this.mob.getX() + Math.cos(angle) * distance;
            double z = this.mob.getZ() + Math.sin(angle) * distance;

            return new Vec3(x, this.mob.getY(), z);
        }
    }
}