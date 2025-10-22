package com.fungoussoup.ancienthorizons.entity.custom.mob;

import com.fungoussoup.ancienthorizons.entity.ModEntities;
import com.fungoussoup.ancienthorizons.registry.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.fungoussoup.ancienthorizons.registry.ModSoundEvents;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ElephantEntity extends TamableAnimal implements NeutralMob {

    private static final EntityDataAccessor<Integer> DATA_REMAINING_ANGER_TIME =
            SynchedEntityData.defineId(ElephantEntity.class, EntityDataSerializers.INT);
    private static final UniformInt PERSISTENT_ANGER_TIME = TimeUtil.rangeOfSeconds(20, 39);

    private UUID persistentAngerTarget;
    private int chargeTime;
    private final Map<UUID, Integer> rememberedPlayers = new HashMap<>(); // -1 for negative, 1 for positive
    private final Map<UUID, Long> playerMemoryTimestamps = new HashMap<>();
    private static final long MEMORY_DURATION = 24000 * 7; // 7 in-game days

    // Constructor
    public ElephantEntity(EntityType<? extends TamableAnimal> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_REMAINING_ANGER_TIME, 0);
    }

    @Override
    public boolean isFood(ItemStack itemStack) {
        return itemStack.is(ModTags.Items.ELEPHANT_FOOD);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.addGoal(2, new RememberGoal(this));
        this.goalSelector.addGoal(3, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.1D));
        this.goalSelector.addGoal(6, new ElephantChargeGoal(this, 1.5D));
        this.goalSelector.addGoal(8, new RandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(10, new RandomLookAroundGoal(this));

        // Target goals
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false, this::shouldAttackPlayer));
        this.targetSelector.addGoal(3, new ResetUniversalAngerTargetGoal<>(this, true));
        this.targetSelector.addGoal(4, new DefendFriendlyPlayerGoal(this));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 50.0)
                .add(Attributes.FOLLOW_RANGE, 40.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.2)
                .add(Attributes.ATTACK_DAMAGE, 12.0)
                .add(Attributes.ATTACK_KNOCKBACK, 1.5);
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        return ModEntities.ELEPHANT.get().create(level());
    }

    @Override
    public boolean hurt(DamageSource damageSource, float amount) {
        if (damageSource.getEntity() instanceof Player player) {
            this.rememberPlayer(player, false); // Remember negative interaction
        }
        return super.hurt(damageSource, amount);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);

        if (this.isFood(itemStack)) {
            InteractionResult result = super.mobInteract(player, hand);
            if (result == InteractionResult.SUCCESS) {
                this.rememberPlayer(player, true); // Remember positive interaction
            }
            return result;
        }

        if (!this.isTame() && isFood(itemStack)) {
            if (!player.getAbilities().instabuild) {
                itemStack.shrink(1);
            }
            if (this.random.nextInt(3) == 0) {
                this.tame(player);
                this.level().broadcastEntityEvent(this, (byte)7); // success
            } else {
                this.level().broadcastEntityEvent(this, (byte)6); // fail
            }
        } else {
            super.usePlayerItem(player, hand, itemStack);
        }

        // Handle riding
        if (!this.level().isClientSide && this.isTame() && this.isOwnedBy(player)) {
            if (this.getPassengers().isEmpty()) {
                player.startRiding(this);
                return InteractionResult.SUCCESS;
            }
        }

        return super.mobInteract(player, hand);
    }

    public boolean canBeControlledByRider() {
        return this.getControllingPassenger() instanceof Player;
    }

    @Override
    public void onPassengerTurned(Entity passenger) {
        this.setRot(this.getYRot(), this.getXRot());
    }

    @Override
    public boolean isVehicle() {
        return !this.getPassengers().isEmpty();
    }

    @Override
    public LivingEntity getControllingPassenger() {
        return this.getFirstPassenger() instanceof LivingEntity living ? living : null;
    }

    @Override
    public void travel(Vec3 travelVector) {
        if (this.isVehicle() && this.canBeControlledByRider() && this.getControllingPassenger() instanceof Player player) {
            // Handle player-controlled movement
            this.setYRot(player.getYRot());
            this.yRotO = this.getYRot();
            this.setXRot(player.getXRot() * 0.5F);
            this.setRot(this.getYRot(), this.getXRot());
            this.yBodyRot = this.getYRot();
            this.yHeadRot = this.yBodyRot;

            float forward = player.zza;
            float strafe = player.xxa;

            if (forward <= 0.0F) {
                forward *= 0.25F; // Slower reverse movement
            }

            // Apply movement
            travelVector = new Vec3(strafe, 0.0D, forward);

            // Set movement speed based on player input
            this.setSpeed((float) this.getAttributeValue(Attributes.MOVEMENT_SPEED) * 0.8F);

            super.travel(travelVector);
        } else {
            super.travel(travelVector);
        }
    }

    @Override
    protected void positionRider(Entity passenger, MoveFunction moveFunction) {
        if (this.hasPassenger(passenger)) {
            double xOffset = 0.0D;
            double zOffset = 0.4D; // Slightly forward on the elephant
            double yOffset = 0.0D;

            if (this.getPassengers().size() > 1) {
                int index = this.getPassengers().indexOf(passenger);
                if (index == 0) {
                    xOffset = 0.2D;
                } else {
                    xOffset = -0.2D;
                }
            }

            Vec3 position = new Vec3(xOffset, yOffset, zOffset).yRot(-this.getYRot() * ((float) Math.PI / 180F));
            moveFunction.accept(passenger, this.getX() + position.x, this.getY() + position.y, this.getZ() + position.z);
        }
    }

    @Override
    public boolean shouldRiderSit() {
        return true;
    }

    // Memory system for player interactions
    public void rememberPlayer(Player player, boolean positive) {
        UUID playerId = player.getUUID();
        this.rememberedPlayers.put(playerId, positive ? 1 : -1);
        this.playerMemoryTimestamps.put(playerId, this.level().getGameTime());
    }

    public boolean hasPositiveMemoryOf(Player player) {
        this.cleanOldMemories();
        return this.rememberedPlayers.getOrDefault(player.getUUID(), 0) > 0;
    }

    public boolean hasNegativeMemoryOf(Player player) {
        this.cleanOldMemories();
        return this.rememberedPlayers.getOrDefault(player.getUUID(), 0) < 0;
    }

    private void cleanOldMemories() {
        long currentTime = this.level().getGameTime();
        this.playerMemoryTimestamps.entrySet().removeIf(entry -> {
            boolean isOld = currentTime - entry.getValue() > MEMORY_DURATION;
            if (isOld) {
                this.rememberedPlayers.remove(entry.getKey());
            }
            return isOld;
        });
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
    public void setPersistentAngerTarget(@Nullable UUID uuid) {
        this.persistentAngerTarget = uuid;
    }

    @Override
    public void startPersistentAngerTimer() {
        this.setRemainingPersistentAngerTime(PERSISTENT_ANGER_TIME.sample(this.random));
    }

    public boolean isAngry() {
        return this.getRemainingPersistentAngerTime() > 0;
    }

    private boolean shouldAttackPlayer(LivingEntity entity) {
        if (!(entity instanceof Player player)) return false;
        return this.isAngryAt(entity) || this.hasNegativeMemoryOf(player);
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if (!this.level().isClientSide) {
            this.updatePersistentAnger((ServerLevel)this.level(), true);
        }

        if (this.chargeTime > 0) {
            --this.chargeTime;
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        this.addPersistentAngerSaveData(tag);
        tag.putInt("ChargeTime", this.chargeTime);

        // Save player memories
        CompoundTag memoriesTag = new CompoundTag();
        for (Map.Entry<UUID, Integer> entry : this.rememberedPlayers.entrySet()) {
            memoriesTag.putInt(entry.getKey().toString(), entry.getValue());
        }
        tag.put("PlayerMemories", memoriesTag);

        CompoundTag timestampsTag = new CompoundTag();
        for (Map.Entry<UUID, Long> entry : this.playerMemoryTimestamps.entrySet()) {
            timestampsTag.putLong(entry.getKey().toString(), entry.getValue());
        }
        tag.put("MemoryTimestamps", timestampsTag);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.readPersistentAngerSaveData(this.level(), tag);
        this.chargeTime = tag.getInt("ChargeTime");

        // Load player memories
        if (tag.contains("PlayerMemories")) {
            CompoundTag memoriesTag = tag.getCompound("PlayerMemories");
            for (String key : memoriesTag.getAllKeys()) {
                try {
                    UUID playerId = UUID.fromString(key);
                    int memory = memoriesTag.getInt(key);
                    this.rememberedPlayers.put(playerId, memory);
                } catch (IllegalArgumentException e) {
                    // Invalid UUID, skip
                }
            }
        }

        if (tag.contains("MemoryTimestamps")) {
            CompoundTag timestampsTag = tag.getCompound("MemoryTimestamps");
            for (String key : timestampsTag.getAllKeys()) {
                try {
                    UUID playerId = UUID.fromString(key);
                    long timestamp = timestampsTag.getLong(key);
                    this.playerMemoryTimestamps.put(playerId, timestamp);
                } catch (IllegalArgumentException e) {
                    // Invalid UUID, skip
                }
            }
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSoundEvents.ELEPHANT_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(@NotNull DamageSource damageSource) {
        return ModSoundEvents.ELEPHANT_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSoundEvents.ELEPHANT_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState block) {
        this.playSound(ModSoundEvents.ELEPHANT_STEP, 0.15F, 1.0F);
    }

    // Custom goal implementations
    private static class RememberGoal extends Goal {
        private final ElephantEntity elephant;

        public RememberGoal(ElephantEntity elephant) {
            this.elephant = elephant;
        }

        @Override
        public boolean canUse() {
            // This goal is always active - it's about remembering interactions
            return true;
        }

        @Override
        public void tick() {
            // Clean old memories periodically
            if (this.elephant.tickCount % 200 == 0) { // Every 10 seconds
                this.elephant.cleanOldMemories();
            }
        }
    }

    private static class DefendFriendlyPlayerGoal extends TargetGoal {
        private final ElephantEntity elephant;
        private LivingEntity targetAttacker;
        private Player defendedPlayer;

        public DefendFriendlyPlayerGoal(ElephantEntity elephant) {
            super(elephant, false);
            this.elephant = elephant;
            this.setFlags(EnumSet.of(Goal.Flag.TARGET));
        }

        public boolean canUse() {
            for (Player player : this.elephant.level().players()) {
                if (!player.isAlive()) continue;

                if (this.elephant.hasPositiveMemoryOf(player)) {
                    LivingEntity lastAttacker = player.getLastHurtByMob();
                    int lastAttackedTime = player.getLastHurtByMobTimestamp();

                    if (lastAttacker != null &&
                            lastAttacker != this.elephant &&
                            lastAttacker.isAlive() &&
                            player.tickCount - lastAttackedTime <= 100 &&
                            this.elephant.distanceToSqr(player) < 64.0D) {

                        this.defendedPlayer = player;
                        this.targetAttacker = lastAttacker;
                        return true;
                    }
                }
            }
            return false;
        }

        @Override
        public void start() {
            if (this.targetAttacker != null) {
                this.elephant.setTarget(this.targetAttacker);
            }
            super.start();
        }

        @Override
        public void stop() {
            this.targetAttacker = null;
            this.defendedPlayer = null;
            super.stop();
        }
    }


    private static class ElephantChargeGoal extends Goal {
        private final ElephantEntity elephant;
        private final double speedModifier;
        private LivingEntity target;
        private int chargeTime;

        public ElephantChargeGoal(ElephantEntity elephant, double speedModifier) {
            this.elephant = elephant;
            this.speedModifier = speedModifier;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            // Don't charge when being ridden
            if (this.elephant.isVehicle()) {
                return false;
            }

            this.target = this.elephant.getTarget();

            if (this.target == null) {
                return false;
            }

            double distance = this.elephant.distanceToSqr(this.target);
            return distance > 16.0D && distance < 144.0D && // Between 4 and 12 blocks
                    this.elephant.chargeTime <= 0 &&
                    this.elephant.hasLineOfSight(this.target);
        }

        @Override
        public boolean canContinueToUse() {
            return this.target != null &&
                    this.target.isAlive() &&
                    this.chargeTime > 0 &&
                    this.elephant.distanceToSqr(this.target) > 4.0D;
        }

        @Override
        public void start() {
            this.chargeTime = 40; // 2 seconds
            this.elephant.chargeTime = 100; // Cooldown
            this.elephant.playSound(ModSoundEvents.ELEPHANT_CHARGE, 1.0F, 1.0F);
        }

        @Override
        public void tick() {
            if (this.target != null && this.chargeTime > 0) {
                this.elephant.getLookControl().setLookAt(this.target, 30.0F, 30.0F);
                this.elephant.getNavigation().moveTo(this.target, this.speedModifier);

                if (this.elephant.distanceToSqr(this.target) < 4.0D) {
                    this.elephant.doHurtTarget(this.target);
                    Vec3 direction = new Vec3(
                            this.target.getX() - this.elephant.getX(),
                            0.0D,
                            this.target.getZ() - this.elephant.getZ()).normalize();

                    this.target.push(direction.x * 2.0F, 0.1F, direction.z * 2.0F);

                }

                --this.chargeTime;
            }
        }

        @Override
        public void stop() {
            this.target = null;
            this.chargeTime = 0;
        }
    }

    public static class ElephantHerdData extends AgeableMob.AgeableMobGroupData {
        public final ElephantEntity elephant;

        public ElephantHerdData(ElephantEntity elephant) {
            super(true); // Set to true for herd behavior
            this.elephant = elephant;
        }
    }
}