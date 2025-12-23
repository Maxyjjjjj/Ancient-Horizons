package com.fungoussoup.ancienthorizons.entity.custom.mob;

import com.fungoussoup.ancienthorizons.registry.ModEntities;
import com.fungoussoup.ancienthorizons.entity.interfaces.DancingAnimal;
import com.fungoussoup.ancienthorizons.registry.ModSoundEvents;
import com.fungoussoup.ancienthorizons.registry.ModTags;
import net.minecraft.ChatFormatting;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.Nullable;

public class RoadrunnerEntity extends TamableAnimal implements DancingAnimal {

    private static final EntityDataAccessor<Boolean> ROADRUNNER_RUNNING = SynchedEntityData.defineId(RoadrunnerEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> SPEED_BOOST_TICKS = SynchedEntityData.defineId(RoadrunnerEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> ROADRUNNER_DANCING = SynchedEntityData.defineId(RoadrunnerEntity.class, EntityDataSerializers.BOOLEAN);

    private int dustCloudTimer = 0;
    private int meepSoundCooldown = 0;
    private boolean wasRunningLastTick = false;
    private boolean isJukeboxing;
    private BlockPos jukeboxPosition;
    public float prevDanceProgress;
    public float danceProgress;

    // Dancing
    public final AnimationState danceAnimationState = new AnimationState();
    private int danceAnimationTimeout = 0;
    private AnimationDefinition currentAnimation = null;

    public RoadrunnerEntity(EntityType<? extends TamableAnimal> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(ROADRUNNER_RUNNING, false);
        builder.define(SPEED_BOOST_TICKS, 0);
        builder.define(ROADRUNNER_DANCING, false);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(3, new PanicGoal(this, 2.5D));
        this.goalSelector.addGoal(4, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(5, new FollowOwnerGoal(this, 1.8D, 10.0F, 2.0F));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));

        // Target goals for tamed roadrunners
        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 14.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.ARMOR, 2.0D)
                .add(Attributes.ATTACK_DAMAGE, 4.0D);
    }

    @Override
    public void tick() {
        super.tick();

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

        // Handle speed boost mechanics
        if (this.getSpeedBoostTicks() > 0) {
            this.setSpeedBoostTicks(this.getSpeedBoostTicks() - 1);
            this.setRunning(true);

            // Create dust particles when running fast
            if (this.level().isClientSide && this.dustCloudTimer++ % 3 == 0) {
                this.createDustCloud();
            }

            // Play meep sound occasionally while running
            if (!this.level().isClientSide && this.meepSoundCooldown <= 0 && this.random.nextInt(60) == 0) {
                this.playMeepSound();
                this.meepSoundCooldown = 40;
            }
        } else {
            this.setRunning(false);
        }

        if (this.meepSoundCooldown > 0) {
            this.meepSoundCooldown--;
        }

        // Check if we started or stopped running
        boolean isRunningNow = this.isRunning();
        if (isRunningNow && !wasRunningLastTick) {
            this.onStartRunning();
        } else if (!isRunningNow && wasRunningLastTick) {
            this.onStopRunning();
        }
        wasRunningLastTick = isRunningNow;

        tickAnimations();
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        Item item = itemstack.getItem();

        if (this.level().isClientSide) {
            if (this.isTame() && this.isOwnedBy(player)) {
                return InteractionResult.SUCCESS;
            } else {
                return itemstack.is(Items.CACTUS) ? InteractionResult.SUCCESS : InteractionResult.PASS;
            }
        } else {
            if (this.isTame()) {
                if (this.isOwnedBy(player)) {
                    if (item == Items.SUGAR) {
                        // Sugar gives speed boost
                        if (!itemstack.isEmpty()) {
                            this.giveSpeedBoost(200); // 10 seconds
                            if (!player.getAbilities().instabuild) {
                                itemstack.shrink(1);
                            }
                            return InteractionResult.SUCCESS;
                        }
                    } else if (this.isFood(itemstack)) {
                        this.heal(this.getMaxHealth() * 0.25f);
                        if (!player.getAbilities().instabuild) {
                            itemstack.shrink(1);
                        }
                        return InteractionResult.SUCCESS;
                    }

                    // Toggle sitting
                    this.setOrderedToSit(!this.isOrderedToSit());
                    this.jumping = false;
                    this.navigation.stop();
                    this.setTarget(null);
                    return InteractionResult.SUCCESS;
                }
            } else if (item == Items.SPIDER_EYE) {
                if (!itemstack.isEmpty()) {
                    if (!player.getAbilities().instabuild) {
                        itemstack.shrink(1);
                    }

                    if (this.random.nextInt(3) == 0) {
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
            }
        }

        return super.mobInteract(player, hand);
    }

    @Override
    public boolean isFood(ItemStack itemStack) {
        return itemStack.is(Items.CACTUS) ||
                itemStack.is(Items.SPIDER_EYE) ||
                itemStack.is(Items.BEETROOT_SEEDS) ||
                itemStack.is(ItemTags.VILLAGER_PLANTABLE_SEEDS);
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        String s = ChatFormatting.stripFormatting(this.getName().getString());

        if ("Meep Meep".equalsIgnoreCase(s) && source.is(DamageTypes.FALLING_ANVIL)) {
            return true;
        }

        if (this.getSpeedBoostTicks() > 0) {
            if (source.is(DamageTypes.CACTUS) ||
                    source.is(DamageTypes.SWEET_BERRY_BUSH) ||
                    source.is(DamageTypes.FALL)) {
                return true;
            }
        }

        return super.isInvulnerableTo(source);
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        RoadrunnerEntity baby = ModEntities.ROADRUNNER.get().create(serverLevel);
        if (baby != null && this.isTame()) {
            baby.setOwnerUUID(this.getOwnerUUID());
            baby.setTame(true, true);
        }
        return baby;
    }

    public static boolean checkRoadrunnerSpawnRules(EntityType<RoadrunnerEntity> entityType, LevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return Animal.checkAnimalSpawnRules(entityType, level, spawnType, pos, random) && level.getBlockState(pos.below()).is(ModTags.Blocks.ROADRUNNERS_SPAWN_ON);
    }

    // Custom methods
    public void giveSpeedBoost(int ticks) {
        this.setSpeedBoostTicks(Math.max(this.getSpeedBoostTicks(), ticks));
        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.6D);
    }

    public boolean isRunning() {
        return this.entityData.get(ROADRUNNER_RUNNING);
    }

    public void setRunning(boolean running) {
        this.entityData.set(ROADRUNNER_RUNNING, running);
    }

    public int getSpeedBoostTicks() {
        return this.entityData.get(SPEED_BOOST_TICKS);
    }

    public void setSpeedBoostTicks(int ticks) {
        this.entityData.set(SPEED_BOOST_TICKS, ticks);
        if (ticks <= 0) {
            this.onStopRunning();
        }
    }

    private void onStartRunning() {
        this.playMeepSound();
    }

    private void onStopRunning() {
        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.35D);
    }

    private void playMeepSound() {
        String name = ChatFormatting.stripFormatting(this.getName().getString());
        this.playSound(("Meep Meep".equalsIgnoreCase(name) ? ModSoundEvents.ROADRUNNER_MEEPMEEP : ModSoundEvents.ROADRUNNER_CRAZY), 1.0F, 1.5F);
    }

    private void createDustCloud() {
        if (this.level().isClientSide) {
            Vec3 motion = this.getDeltaMovement();
            for (int i = 0; i < 3; i++) {
                double offsetX = (this.random.nextDouble() - 0.5) * 0.5;
                double offsetZ = (this.random.nextDouble() - 0.5) * 0.5;
                this.level().addParticle(
                        net.minecraft.core.particles.ParticleTypes.POOF,
                        this.getX() + offsetX,
                        this.getY() + 0.1,
                        this.getZ() + offsetZ,
                        -motion.x * 0.1,
                        0.1,
                        -motion.z * 0.1
                );
            }
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSoundEvents.ROADRUNNER_IDLE; // Replace with custom sound
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ModSoundEvents.ROADRUNNER_HURT; // Replace with custom sound
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSoundEvents.ROADRUNNER_DEATH; // Replace with custom sound
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState block) {
        this.playSound(SoundEvents.CHICKEN_STEP, 0.15F, 1.0F);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("SpeedBoostTicks", this.getSpeedBoostTicks());
        compound.putBoolean("Running", this.isRunning());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setSpeedBoostTicks(compound.getInt("SpeedBoostTicks"));
        this.setRunning(compound.getBoolean("Running"));
    }

    @Override
    public Vec3 getLeashOffset() {
        return new Vec3(0.0D, 0.6F * this.getEyeHeight(), this.getBbWidth() * 0.4F);
    }

    // Make roadrunners faster in desert biomes
    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level().getBiome(this.blockPosition()).is(BiomeTags.IS_BADLANDS) || this.level().getBiome(this.blockPosition()).is(Tags.Biomes.IS_DESERT)) {
            if (this.getSpeedBoostTicks() <= 0 && this.random.nextInt(200) == 0) {
                this.giveSpeedBoost(100); // Natural speed boost in hot biomes
            }
        }
    }

    public boolean isDancing() {
        return this.entityData.get(ROADRUNNER_DANCING);
    }

    public void setDancing(boolean dancing) {
        this.entityData.set(ROADRUNNER_DANCING, dancing);
        this.isJukeboxing = dancing;
    }

    @Override
    public void setJukeboxPos(BlockPos pos) {
        this.jukeboxPosition = pos;
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

    private void tickAnimations() {
        // Dance state: active if the entity is flagged as dancing
        if (this.isDancing()) {
            this.danceAnimationState.start(tickCount);
        } else {
            this.danceAnimationState.stop();
        }
    }

    public AnimationState getDanceAnimationState() {
        return this.danceAnimationState;
    }
}