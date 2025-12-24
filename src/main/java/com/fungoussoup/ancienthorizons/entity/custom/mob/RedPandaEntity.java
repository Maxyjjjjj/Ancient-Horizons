package com.fungoussoup.ancienthorizons.entity.custom.mob;

import com.fungoussoup.ancienthorizons.entity.interfaces.ArborealAnimal;
import com.fungoussoup.ancienthorizons.entity.interfaces.SleepingAnimal;
import com.fungoussoup.ancienthorizons.registry.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class RedPandaEntity extends Animal implements SleepingAnimal, ArborealAnimal {
    private static final EntityDataAccessor<Boolean> SLEEPING = SynchedEntityData.defineId(RedPandaEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> SITTING = SynchedEntityData.defineId(RedPandaEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> CLIMBING = SynchedEntityData.defineId(RedPandaEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> STANDING_UP = SynchedEntityData.defineId(RedPandaEntity.class, EntityDataSerializers.BOOLEAN);

    public final AnimationState sleepingAnimationState = new AnimationState();
    public final AnimationState standingUpAnimationState = new AnimationState();

    private int sleepTimer = 0;
    private int sitTimer = 0;
    private int standUpTimer = 0;

    public RedPandaEntity(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
        this.setCanPickUpLoot(false);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 14.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.FOLLOW_RANGE, 16.0D)
                .add(Attributes.ATTACK_DAMAGE, 2.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new AvoidEntityGoal<>(this, SnowLeopardEntity.class, 10.0F, 1.5D, 1.8D));
        this.goalSelector.addGoal(2, new PanicGoal(this, 1.5D));
        this.goalSelector.addGoal(3, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(4, new TemptGoal(this, 1.1D, stack -> this.isFood(stack), false));
        this.goalSelector.addGoal(5, new FollowParentGoal(this, 1.1D));
        this.goalSelector.addGoal(6, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(SLEEPING, false);
        builder.define(SITTING, false);
        builder.define(CLIMBING, false);
        builder.define(STANDING_UP, false);
    }

    @Override
    public void setSleeping(boolean sleeping) {
        this.entityData.set(SLEEPING, sleeping);
    }

    public boolean isSleeping() {
        return this.entityData.get(SLEEPING);
    }

    public void setSitting(boolean sitting) {
        this.entityData.set(SITTING, sitting);
    }

    public boolean isSitting() {
        return this.entityData.get(SITTING);
    }

    public void setClimbing(boolean climbing) {
        this.entityData.set(CLIMBING, climbing);
    }

    public boolean isClimbing() {
        return this.entityData.get(CLIMBING);
    }

    public void setStandingUp(boolean standingUp) {
        this.entityData.set(STANDING_UP, standingUp);
    }

    public boolean isStandingUp() {
        return this.entityData.get(STANDING_UP);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide) {
            // Handle animation states on client side
            this.sleepingAnimationState.animateWhen(this.isSleeping(), this.tickCount);
            this.standingUpAnimationState.animateWhen(this.isStandingUp(), this.tickCount);
        }

        if (!this.level().isClientSide) {
            // Check for nearby snow leopards
            boolean snowLeopardNearby = !this.level().getEntitiesOfClass(
                    SnowLeopardEntity.class,
                    this.getBoundingBox().inflate(10.0D)
            ).isEmpty();

            // Stand up on hind legs when snow leopard is nearby (defensive posture)
            if (snowLeopardNearby) {
                if (!this.isStandingUp()) {
                    this.setStandingUp(true);
                    this.standUpTimer = 0;
                    // Stop other behaviors
                    if (this.isSleeping()) {
                        this.setSleeping(false);
                        this.sleepTimer = 0;
                    }
                    if (this.isSitting()) {
                        this.setSitting(false);
                        this.sitTimer = 0;
                    }
                }
                this.standUpTimer++;
                // Stay standing while threat is present
            } else if (this.isStandingUp()) {
                // Gradually stop standing after threat is gone
                this.standUpTimer++;
                if (this.standUpTimer > 60) { // Stand for 3 seconds after threat leaves
                    this.setStandingUp(false);
                    this.standUpTimer = 0;
                }
            }

            // Don't do other idle behaviors while standing up defensively
            if (this.isStandingUp()) {
                return;
            }

            // Sleep cycle management
            if (this.isSleeping()) {
                this.sleepTimer++;
                if (this.sleepTimer > 200) { // Sleep for ~10 seconds
                    this.setSleeping(false);
                    this.sleepTimer = 0;
                }
            } else if (this.random.nextInt(1000) == 0 && !this.isInWaterOrBubble()) {
                this.setSleeping(true);
            }

            // Sitting behavior
            if (this.isSitting()) {
                this.sitTimer++;
                if (this.sitTimer > 100) {
                    this.setSitting(false);
                    this.sitTimer = 0;
                }
            } else if (this.random.nextInt(800) == 0 && !this.isInWaterOrBubble()) {
                this.setSitting(true);
            }

            // Tree climbing detection
            this.setClimbing(this.horizontalCollision && this.isOnLeavesOrLog());
        }
    }

    private boolean isOnLeavesOrLog() {
        BlockPos pos = this.blockPosition();
        BlockState state = this.level().getBlockState(pos);
        return state.is(BlockTags.LEAVES) || state.is(BlockTags.LOGS);
    }

    @Override
    public boolean onClimbable() {
        return this.isClimbing();
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);

        if (this.isFood(itemstack)) {
            if (!player.getAbilities().instabuild) {
                itemstack.shrink(1);
            }

            if (!this.level().isClientSide) {
                // Heal the panda
                this.heal(2.0F);

                // Small chance to make it sit
                if (this.random.nextFloat() < 0.33F) {
                    this.setSitting(true);
                    this.sitTimer = 0;
                }
            }

            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }

        return super.mobInteract(player, hand);
    }

    @Override
    public boolean isFood(ItemStack itemStack) {
        return itemStack.is(Items.BAMBOO) ||
                itemStack.is(ItemTags.LEAVES) ||
                itemStack.is(Items.APPLE) ||
                itemStack.is(Items.SWEET_BERRIES) ||
                itemStack.is(Items.GLOW_BERRIES) ||
                itemStack.is(ItemTags.SMALL_FLOWERS);
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        return ModEntities.RED_PANDA.get().create(serverLevel);
    }

    public static boolean checkRedPandaSpawnRules(EntityType<RedPandaEntity> entityType, LevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return level.getBlockState(pos.below()).is(BlockTags.ANIMALS_SPAWNABLE_ON) && isBrightEnoughToSpawn(level, pos);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.PANDA_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.PANDA_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.PANDA_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.PANDA_STEP, 0.15F, 1.0F);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("Sleeping", this.isSleeping());
        tag.putBoolean("Sitting", this.isSitting());
        tag.putBoolean("StandingUp", this.isStandingUp());
        tag.putInt("SleepTimer", this.sleepTimer);
        tag.putInt("SitTimer", this.sitTimer);
        tag.putInt("StandUpTimer", this.standUpTimer);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.setSleeping(tag.getBoolean("Sleeping"));
        this.setSitting(tag.getBoolean("Sitting"));
        this.setStandingUp(tag.getBoolean("StandingUp"));
        this.sleepTimer = tag.getInt("SleepTimer");
        this.sitTimer = tag.getInt("SitTimer");
        this.standUpTimer = tag.getInt("StandUpTimer");
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) {
        // Red pandas are excellent climbers and take reduced fall damage
        return super.causeFallDamage(fallDistance * 0.5F, multiplier, source);
    }

    // Custom goal for sitting behavior
    static class SitWhenOrderedToGoal extends Goal {
        private final RedPandaEntity panda;

        public SitWhenOrderedToGoal(RedPandaEntity panda) {
            this.panda = panda;
        }

        @Override
        public boolean canUse() {
            return this.panda.isSitting();
        }

        @Override
        public void start() {
            this.panda.getNavigation().stop();
        }
    }
}