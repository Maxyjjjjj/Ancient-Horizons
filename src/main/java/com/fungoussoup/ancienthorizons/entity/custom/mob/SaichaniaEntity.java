package com.fungoussoup.ancienthorizons.entity.custom.mob;

import com.fungoussoup.ancienthorizons.registry.ModEntities;
import com.fungoussoup.ancienthorizons.registry.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class SaichaniaEntity extends TamableAnimal implements PlayerRideable {

    // Data trackers for syncing with client
    private static final EntityDataAccessor<Boolean> DATA_DEFENDING =
            SynchedEntityData.defineId(SaichaniaEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_ATTACK_TICK =
            SynchedEntityData.defineId(SaichaniaEntity.class, EntityDataSerializers.INT);

    // Animation state management
    private int defendCooldown = 0;
    private int idleAnimationTimeout = 0;
    private int eatAnimationTick = 0;
    private int lastHurtByMobTimestamp = 0;

    // Behavior variables
    private boolean isGrazing = false;
    private int grazingTicks = 0;
    private static final int GRAZING_DURATION = 100;


    public SaichaniaEntity(EntityType<? extends TamableAnimal> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_DEFENDING, false);
        builder.define(DATA_ATTACK_TICK, 0);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.4D));
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new TemptGoal(this, 1.2D, stack -> stack.is(ModTags.Items.PREHISTORIC_HERBIVORE_FOOD), false));
        this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.1D));
        this.goalSelector.addGoal(5, new FollowOwnerGoal(this, 1.0D, 10.0F, 2.0F));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));

        // Defensive behaviour - becomes aggressive when attacked
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 40.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.2D)
                .add(Attributes.ARMOR, 8.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.6D)
                .add(Attributes.ATTACK_DAMAGE, 16.0D);
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        GroundPathNavigation navigation = new GroundPathNavigation(this, level);
        navigation.setCanFloat(true);
        return navigation;
    }

    // ==================== ANIMATION & BEHAVIOR ====================

    @Override
    public void tick() {
        super.tick();

        // Handle defend cooldown
        if (defendCooldown > 0) {
            defendCooldown--;
        }

        if (this.level().isClientSide) {
            if (this.getAttackTick() > 0) {
                this.entityData.set(DATA_ATTACK_TICK, this.getAttackTick() - 1);
            }
        }

        // Handle idle animation timeout
        if (this.idleAnimationTimeout > 0) {
            this.idleAnimationTimeout--;
        }

        // Trigger defend pose when hurt
        if (this.hurtTime > 0 && !isDefending()) {
            startDefending();
        } else if (this.hurtTime == 0 && isDefending() && defendCooldown == 0) {
            stopDefending();
        }

        // Grazing behavior
        if (!this.level().isClientSide) {
            handleGrazing();
        }

        // Eating animation
        if (this.eatAnimationTick > 0) {
            this.eatAnimationTick--;
        }
    }

    public int getAttackTick() {
        return this.entityData.get(DATA_ATTACK_TICK);
    }

    private void handleGrazing() {
        if (this.isGrazing) {
            grazingTicks++;
            if (grazingTicks >= GRAZING_DURATION) {
                stopGrazing();
            }
        } else if (this.random.nextInt(1000) == 0 && this.onGround() && !this.isInWater()) {
            // Randomly start grazing
            BlockPos below = this.blockPosition().below();
            BlockState state = this.level().getBlockState(below);
            if (state.is(net.minecraft.tags.BlockTags.DIRT) ||
                    state.is(net.minecraft.tags.BlockTags.LEAVES)) {
                startGrazing();
            }
        }
    }

    public void startGrazing() {
        this.isGrazing = true;
        this.grazingTicks = 0;
        this.getNavigation().stop();
    }

    public void stopGrazing() {
        this.isGrazing = false;
        this.grazingTicks = 0;
    }

    public boolean isGrazing() {
        return this.isGrazing;
    }

    public void startDefending() {
        if (defendCooldown == 0) {
            this.entityData.set(DATA_DEFENDING, true);
            defendCooldown = 100; // 5 seconds cooldown
            this.playSound(SoundEvents.IRON_GOLEM_DAMAGE, 1.0F, 1.2F);
        }
    }

    public void stopDefending() {
        this.entityData.set(DATA_DEFENDING, false);
    }

    public boolean isDefending() {
        return this.entityData.get(DATA_DEFENDING);
    }

    // ==================== COMBAT ====================

    @Override
    public boolean hurt(DamageSource source, float amount) {
        // Reduced damage when defending
        if (isDefending()) {
            amount *= 0.5F; // 50% damage reduction when in defend pose
        }

        // Additional armour protection
        amount *= 0.75F; // Heavy armour reduces all damage by 25%

        boolean hurt = super.hurt(source, amount);

        if (hurt && source.getEntity() instanceof LivingEntity) {
            this.lastHurtByMobTimestamp = this.tickCount;
        }

        return hurt;
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        boolean hit = super.doHurtTarget(target);

        this.entityData.set(DATA_ATTACK_TICK, 10);

        if (hit && target instanceof LivingEntity livingTarget) {
            // Tail club attack - knockback effect
            double knockbackStrength = 1.5D;
            double dx = livingTarget.getX() - this.getX();
            double dz = livingTarget.getZ() - this.getZ();
            livingTarget.knockback(knockbackStrength, dx, dz);

            this.playSound(SoundEvents.IRON_GOLEM_ATTACK, 1.0F, 1.0F);
        }

        return hit;
    }

    // ==================== INTERACTION & TAMING ====================

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);

        // Feeding for taming
        if (!this.isTame() && this.isFood(itemStack)) {
            if (!player.getAbilities().instabuild) {
                itemStack.shrink(1);
            }

            this.eatAnimationTick = 40;

            // Show hearts
            if (this.level().isClientSide) {
                this.level().addParticle(
                        net.minecraft.core.particles.ParticleTypes.HEART,
                        this.getRandomX(1.0D),
                        this.getRandomY() + 0.5D,
                        this.getRandomZ(1.0D),
                        0.0D, 0.0D, 0.0D
                );
            }

            this.gameEvent(net.minecraft.world.level.gameevent.GameEvent.EAT);
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }

        // Healing when tamed
        if (this.isTame() && this.isFood(itemStack) && this.getHealth() < this.getMaxHealth()) {
            if (!player.getAbilities().instabuild) {
                itemStack.shrink(1);
            }
            this.heal(10.0F);
            this.eatAnimationTick = 40;
            this.gameEvent(net.minecraft.world.level.gameevent.GameEvent.EAT);
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }

        return super.mobInteract(player, hand);
    }

    // ==================== BREEDING & VARIANTS ====================

    @Override
    public boolean isFood(ItemStack itemStack) {
        return itemStack.is(ModTags.Items.PREHISTORIC_HERBIVORE_FOOD);
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob parent) {
        SaichaniaEntity baby = ModEntities.SAICHANIA.get().create(serverLevel);
        if (baby != null) {
            if (this.isTame()) {
                baby.setTame(true, true);
                baby.setOwnerUUID(this.getOwnerUUID());
            }
        }
        return baby;
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty,
                                        MobSpawnType reason, @Nullable SpawnGroupData spawnData) {
        return super.finalizeSpawn(level, difficulty, reason, spawnData);
    }

    // ==================== SOUNDS ====================

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.HOGLIN_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.HOGLIN_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.HOGLIN_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.IRON_GOLEM_STEP, 0.15F, 1.0F);
    }

    @Override
    protected float getSoundVolume() {
        return 0.6F;
    }

    // ==================== ARMOR & DEFENSE ====================

    @Override
    public int getArmorValue() {
        return isDefending() ? 12 : 8; // Extra armour when defending
    }

    public float getDefenseMultiplier() {
        return isDefending() ? 0.5F : 0.75F;
    }

    // ==================== ANIMATION HELPERS ====================

    public float getWalkAnimationSpeed() {
        return this.isBaby() ? 1.5F : 1.0F;
    }

    public float getAnimationScale() {
        return this.isBaby() ? 0.5F : 1.0F;
    }

    public int getEatAnimationTick() {
        return this.eatAnimationTick;
    }

    public float getGrazingProgress(float partialTick) {
        if (!this.isGrazing) return 0.0F;
        return Math.min(1.0F, (this.grazingTicks + partialTick) / 40.0F);
    }

    // ==================== NBT PERSISTENCE ====================

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("Defending", this.isDefending());
        compound.putInt("DefendCooldown", this.defendCooldown);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.entityData.set(DATA_DEFENDING, compound.getBoolean("Defending"));
        this.defendCooldown = compound.getInt("DefendCooldown");
    }

    // ==================== MOVEMENT ====================

    @Override
    public Vec3 getLeashOffset() {
        return new Vec3(0.0D, (double)(0.6F * this.getEyeHeight()), (double)(this.getBbWidth() * 0.4F));
    }

    @Override
    protected float getWaterSlowDown() {
        return 0.9F; // Heavy creature, slower in water
    }

    public boolean canDisableShield() {
        return true;
    }

    @Override
    public int getMaxHeadYRot() {
        return 40; // Limited head rotation due to armour
    }

    @Override
    public boolean isPushable() {
        return !this.isDefending(); // Cannot be pushed while defending
    }
}